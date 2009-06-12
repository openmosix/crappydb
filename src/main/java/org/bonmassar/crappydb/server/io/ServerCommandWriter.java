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

public class ServerCommandWriter implements OutputCommandWriter {
	
	private LinkedList<ByteBuffer> bufferList;
	private SelectionKey requestsDescriptor;
	
	private Logger logger = Logger.getLogger(ServerCommandWriter.class);
	
	public ServerCommandWriter(SelectionKey requestsDescriptor) {
		this.requestsDescriptor = requestsDescriptor;
		bufferList = new LinkedList<ByteBuffer>();
	}
	
	public void writeToOutstanding(byte[] data) {
		addToQueue(newBufferItem(data));
	}
	
	public void writeToOutstanding(String text) {
		if(null == text || 0 == text.length())
			return;
		
		writeToOutstanding(text.getBytes());
	}
	
	public void write() throws IOException {
		SocketChannel sc = (SocketChannel)requestsDescriptor.channel();
		if(!sc.isOpen()) {
			logger.warn("write closed");
			return;
		}
		
		writeToSocketChannel(sc);
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
		
		sc.write(buffer);
		
		return continueWritingIfCurrentCompleted(buffer);
	}

	private boolean continueWritingIfCurrentCompleted(ByteBuffer buffer) {
		if(!buffer.hasRemaining()) 
			return true;
		
		logger.debug("write blocked");
		requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		return false;
	}

}
