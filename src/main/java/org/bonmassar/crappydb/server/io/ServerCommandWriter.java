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
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;

public abstract class ServerCommandWriter implements OutputCommandWriter {

	protected SelectionKey requestsDescriptor;
	protected LinkedList<ByteBuffer> bufferList;
	
	protected Logger logger = Logger.getLogger(ServerCommandWriter.class);
	protected String connectionid;
	
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
	
	public abstract void write() throws IOException;
	
	public void setConnectionId(String id) {
		connectionid = id;
	}
	
	private ByteBuffer newBufferItem(byte[] data) {
		ByteBuffer bb = ByteBuffer.allocate(data.length);
		bb.clear();
		bb.put(data).flip();
		return bb;
	}

	protected void addToQueue(ByteBuffer bb) {
		synchronized(bufferList){
			bufferList.addLast(bb);
			requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}

}
