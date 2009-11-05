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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.stats.DBStats;

public abstract class BufferReader {
	
	protected final static Logger logger = Logger.getLogger(BufferReader.class);
	
	private static final byte CR = 0x0D;
	private static final byte LF = 0x0A;
	
	private SelectionKey selectionKey;
	protected ByteBuffer buffer;
	private int lastLengthRead;
	protected String connectionid;
	
	public BufferReader(SelectionKey selectionKey){
		this.selectionKey = selectionKey;
		lastLengthRead = 0;
		connectionid = "unknown";
	}
	
	public void precacheDataFromRemote() throws IOException{
		ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel();
		if (invalidSocket(channel))
			throw new IOException("Read descriptor is closed");

		read(channel);
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
	
	public byte[] getBytes(int nBytes){
		if( nBytes <= 0 )
			return new byte[0];
		
		int nBytesToRead = getMaximumBytesLength(nBytes);
		byte[] tmpBuffer = new byte[nBytesToRead];
		buffer.get(tmpBuffer);
		DBStats.INSTANCE.getConnections().newReceive(nBytesToRead);
		return tmpBuffer;
	}
	
	public void setConnectionId(String id) {
		connectionid = id;
	}

	protected abstract int channelRead(ReadableByteChannel channel) throws IOException;

	private int getMaximumBytesLength(int nBytes) {
		return (nBytes > buffer.remaining()) ? buffer.remaining() : nBytes;
	}
	
	protected boolean invalidSocket(ReadableByteChannel channel) {
		return null == channel || !channel.isOpen();
	}
	
	private boolean read(ReadableByteChannel channel) throws IOException{
		buffer.clear();
		lastLengthRead = channelRead(channel);
		checkInvalidRead();
		if(logger.isDebugEnabled())
			logger.debug(String.format("[<= ] [%s] Received %d bytes", connectionid, lastLengthRead));
		
		return lastLengthRead > 0;
	}

	private void checkInvalidRead() throws ClosedChannelException{
		if (lastLengthRead < 0)
			throw new ClosedChannelException();
	}
	
	private int getEndOfLinePosition(){
		if(brokenLinePacketBeginning())
			return 0;
		
		for(int i = buffer.position()+1; i < buffer.limit(); i++){
			if(BufferReader.LF == buffer.get(i) && BufferReader.CR == buffer.get(i-1))
				return i;
		}
		return -1;
	}

	private boolean brokenLinePacketBeginning() {
		return buffer.position() == 0 && BufferReader.LF == buffer.get(0);
	}
}
