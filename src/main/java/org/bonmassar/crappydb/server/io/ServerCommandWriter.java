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
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.stats.DBStats;

class ServerCommandWriter implements OutputCommandWriter {

	private SelectionKey requestsDescriptor;
	protected LinkedList<ByteBuffer> bufferList;
	
	private Logger logger = Logger.getLogger(ServerCommandWriter.class);
	private String connectionid;
	
	public ServerCommandWriter(SelectionKey requestsDescriptor) {
		this.requestsDescriptor = requestsDescriptor;
		bufferList = new LinkedList<ByteBuffer>();
		connectionid = "unknown";
	}
	
	public void writeToOutstanding(byte[] data) {
		if(null == data || 0 == data.length)
			return;

		addToQueue(newBufferItem(data));
	}
	
	public void writeToOutstanding(String text) {		
		writeToOutstanding(text.getBytes());
	}

	public void writeException(CrappyDBException exception) {
		if(null == exception)
			return;
		
		writeToOutstanding(exception.clientResponse()+"\r\n");
	}
	
	public void write() throws IOException {
		requestsDescriptor.interestOps(SelectionKey.OP_READ);

		SocketChannel sc = (SocketChannel)requestsDescriptor.channel();
		assertOpenChannel(sc); 
		
		writeToSocketChannel(sc);
	}
	
	public void setConnectionId(String id) {
		connectionid = id;
	}

	protected void assertOpenChannel(SocketChannel sc) throws IOException {
		if(null != sc && sc.isOpen())
			return;
		
		logger.warn(String.format("[ =>] [%s] Write closed", connectionid));
		throw new IOException("Channel closed while writing");
	}
	
	private ByteBuffer newBufferItem(byte[] data) {
		ByteBuffer bb = ByteBuffer.allocate(data.length);
		bb.clear();
		bb.put(data).flip();
		return bb;
	}

	private void addToQueue(ByteBuffer bb) {
		synchronized(bufferList){
			bufferList.addLast(bb);
			requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}

	private void writeToSocketChannel(SocketChannel sc) throws IOException {
		synchronized(bufferList){
			if(0 == bufferList.size())
				return;
			
			for(Iterator<ByteBuffer> bit = bufferList.iterator(); bit.hasNext();){
				if(!writeBufferElementToSocketChannel(sc, bit.next()))
					break;
				bit.remove();
			}
		}
	}
	
	private boolean writeBufferElementToSocketChannel(SocketChannel sc, ByteBuffer buffer) throws IOException {
		if(!buffer.hasRemaining())
			return true;
		
		int nbytes = sc.write(buffer);
		if(logger.isDebugEnabled())
			logger.debug(String.format("[ =>] [%s] Sent %d bytes", connectionid, nbytes));
		
		DBStats.INSTANCE.getConnections().newSend(nbytes);
		return continueWritingIfCurrentCompleted(buffer);
	}

	private boolean continueWritingIfCurrentCompleted(ByteBuffer buffer) {
		if(!buffer.hasRemaining()) 
			return true;
		
		if(logger.isDebugEnabled())
			logger.debug(String.format("[ =>] [%s] Write blocked", connectionid));
		
		requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		return false;
	}

}
