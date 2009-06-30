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

public class InputPipe {
	private static final byte CR = 0x0D;
	private static final byte LF = 0x0A;
	protected static final int maxChunkSize = 8 * 1024;
	
	private SelectionKey requestsDescriptor;
	protected ByteBuffer buffer;
	private int lastLengthRead;
	private Logger logger = Logger.getLogger(InputPipe.class);
	private String connectionid;
	
	public InputPipe(SelectionKey requestsDescriptor){
		this.requestsDescriptor = requestsDescriptor;
		buffer = ByteBuffer.allocate(InputPipe.maxChunkSize);
		lastLengthRead = 0;
		connectionid = "unknown";
	}
	
	public void precacheDataFromRemote() throws IOException{
		SocketChannel channel = (SocketChannel) requestsDescriptor.channel();
		if (invalidSocket(channel))
			throw new IOException("Read descriptor is closed");

		getReceivedData(channel);
	}
	
	public boolean noDataAvailable(){
		return null == buffer || 0 == buffer.remaining() || lastLengthRead <= 0 ;
	}
	
	public String readTextLine() {
		int crlfposition = getEndOfLinePosition();
		if(-1 == crlfposition)
			return "";

		byte[] rawString = getBytes(crlfposition+1-buffer.position());
		if(null == rawString || 0 == rawString.length)
			return "";
		
		return new String(rawString);
	}
	
	public String getRemainingDataAsText() {
		byte[] data = getBytes(buffer.remaining());
		if(null!=data)
			return new String(data);
		
		return "";
	}
	
	public byte[] getBytes(int nBytes){
		if( nBytes <= 0 )
			return new byte[0];
		
		int nBytesToRead = getMaximumBytesLength(nBytes);
		byte[] tmpBuffer = new byte[nBytesToRead];
		buffer.get(tmpBuffer);
		return tmpBuffer;
	}

	private int getMaximumBytesLength(int nBytes) {
		return (nBytes > buffer.remaining()) ? buffer.remaining() : nBytes;
	}
	
	protected boolean invalidSocket(SocketChannel channel) {
		return null == channel || !channel.isOpen();
	}
	
	private void getReceivedData(SocketChannel channel) throws IOException {
		boolean executed = read(channel);
		if (!executed) 
			return;
		
		buffer.flip();
	}
	
	private boolean read(SocketChannel channel) throws IOException{
		buffer.clear();
		lastLengthRead = channelRead(channel);
		if(logger.isDebugEnabled())
			logger.debug(String.format("[<= ] [%s] Received %d bytes", connectionid, lastLengthRead));
		checkInvalidRead();
		
		return lastLengthRead > 0;
	}

	protected Integer channelRead(SocketChannel channel) throws IOException {
		//Mockito does not stub properly function that returns boolean/int
		try{
			return channel.read(buffer);
		}catch(java.nio.BufferOverflowException boe){
			logger.fatal(String.format("[<= ] [%s] Buffer overflow writing data into chunk buffer", connectionid), boe);
			throw new IOException("Chunk too large");
		}
	}
	
	private void checkInvalidRead() throws IOException{
		if (lastLengthRead < 0)
			throw new IOException(String.format("[<= ] [%s] Error reading from stream", connectionid));
	}
	
	private int getEndOfLinePosition(){
		if(brokenLinePacketBeginning())
			return 0;
		
		for(int i = buffer.position()+1; i < buffer.limit(); i++){
			if(InputPipe.LF == buffer.get(i) && InputPipe.CR == buffer.get(i-1))
				return i;
		}
		return -1;
	}

	private boolean brokenLinePacketBeginning() {
		return buffer.position() == 0 && InputPipe.LF == buffer.get(0);
	}

	public void setConnectionId(String id) {
		connectionid = id;
	}


}
