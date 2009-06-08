/*
 *  This file is part of CrappyDB-Server, 
 *  developed by Luca Bonmassar <luca.bonmassar at gmail.com>
 *
 *  CrappyDB-Server is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  CrappyDB-Server is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CrappyDB-Server.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bonmassar.crappydb.server.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.bonmassar.crappydb.server.storage.memory.UnboundedMap;

public class ServerCommandReader {
	private static final byte CR = 0x0D;
	private static final byte LF = 0x0A;
	private static final int buffSize = 8 * 1024;
	private SelectionKey requestsDescriptor;
	private ByteBuffer buffer;
	private int contentLength;
	private ServerCommand decodedCmd;
	private static CommandFactory cmdFactory = new CommandFactory(new UnboundedMap());

	private Logger logger = Logger.getLogger(ServerCommandReader.class); 
	
	public ServerCommandReader(SelectionKey requestsDescriptor) {
		this.requestsDescriptor = requestsDescriptor;
		buffer = ByteBuffer.allocate(ServerCommandReader.buffSize);
		contentLength = 0;
		decodedCmd = null;
	}

	public ServerCommand decodeCommand() throws CrappyDBException, IOException {
		SocketChannel channel = (SocketChannel) requestsDescriptor.channel();
		if (!channel.isOpen())
			throw new IOException("Read descriptor is closed");
			
		getReceivedData(channel);
		return decodeIncomingData();
	}
	
	private void getReceivedData(SocketChannel channel) throws IOException {
		boolean executed = read(channel);
		if (!executed) 
			return;
		
		buffer.flip();
	}
	
	private boolean read(SocketChannel channel) throws IOException{
		buffer.clear();
		int len = channel.read(buffer);
		logger.debug(String.format("read len=%d", len));
		checkInvalidRead(len);
		
		return len > 0;
	}
	
	private void checkInvalidRead(int len) throws IOException{
		if (len < 0)
			throw new IOException("Error reading from stream");
	}

	private ServerCommand decodeIncomingData() throws ErrorException {
		if (null == buffer || buffer.remaining() == 0) 
			return null;
		
		if(null == decodedCmd)
			decodeCommandFromIncomingData();
		
		if(null == decodedCmd)
			return null;
				
		if(0 == contentLength)
			return reset();
		
		if(buffer.remaining() > 0)
			decodedCmd.addPayloadContentPart(getPayloadData());
		
		if(0 == contentLength)
			return reset();
		
		return null;
	}
	
	private void decodeCommandFromIncomingData() throws ErrorException {
		int crlfpos = getEndOfLinePosition();
		if(-1 == crlfpos)
			return;
		
		String receivedCommand = readStringStopCrLf(crlfpos);		
		logger.debug("cmd: "+receivedCommand);
		
		decodedCmd = cmdFactory.getCommandFromCommandLine(receivedCommand);	
		contentLength = decodedCmd.payloadContentLength();
	}

	private ServerCommand reset() {
		contentLength = 0;
		ServerCommand cmd = decodedCmd;
		decodedCmd = null;
		return cmd;
	}
	
	private byte[] getPayloadData() {
		int toRead = (contentLength > buffer.remaining()) ? buffer.remaining() : contentLength;
		contentLength -= toRead;
		byte[] read = new byte[toRead];
		
		buffer.get(read);
		
		return read;
	}

	private String readStringStopCrLf(int crlfpos) {
		byte[] bytebuffer = new byte[crlfpos+1];
		buffer.get(bytebuffer, 0, crlfpos+1);
		return new String(bytebuffer);
	}

	private int getEndOfLinePosition(){
		for(int i = 0; i < buffer.remaining()-1; i++){
			if(CR == buffer.get(i) && LF == buffer.get(i+1))
				return i+1;
		}
		return -1;
	}
}
