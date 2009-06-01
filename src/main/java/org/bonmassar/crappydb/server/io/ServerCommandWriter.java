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

public class ServerCommandWriter implements OutputCommandWriter {
	
	private LinkedList<ByteBuffer> bufferList;
	private SelectionKey requestsDescriptor;
	
	public ServerCommandWriter(SelectionKey requestsDescriptor) {
		this.requestsDescriptor = requestsDescriptor;
		bufferList = new LinkedList<ByteBuffer>();
	}
	
	public void write(byte[] data) {
		synchronized(bufferList){
			ByteBuffer bb = ByteBuffer.allocate(data.length);
			bb.clear();
			bb.put(data);
			bb.flip();
			bufferList.addLast(bb);

			requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}
	
	public void flushOnSocket() throws IOException {
		SocketChannel sc = (SocketChannel)requestsDescriptor.channel();
		if(!sc.isOpen()) {
			System.out.println("write closed");
			return;
		}
		
		synchronized(bufferList){
			if(0 == bufferList.size())
				return;
			
			for(Iterator<ByteBuffer> bit = bufferList.iterator(); bit.hasNext();){
				ByteBuffer buffer  = bit.next();
				if(!flushOnSocket(sc, buffer))
					break;
				bit.remove();
			}
		}
	}

	private boolean flushOnSocket(SocketChannel sc, ByteBuffer buffer) throws IOException {
		if(buffer.hasRemaining())
			sc.write(buffer);
		else 
			return true;
		
		if(buffer.hasRemaining()) {
			System.out.println("write blocked");
			requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			return false;
		}
		return true;
	}

}
