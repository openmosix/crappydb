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

package org.bonmassar.crappydb.server.io.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.bonmassar.crappydb.server.io.ServerCommandWriter;
import org.bonmassar.crappydb.server.stats.DBStats;

public class TcpCommandWriter extends ServerCommandWriter {

	public TcpCommandWriter(SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
	}

	public void write() throws IOException {
		requestsDescriptor.interestOps(SelectionKey.OP_READ);

		SocketChannel sc = (SocketChannel)requestsDescriptor.channel();
		assertOpenChannel(sc); 
		
		writeToSocketChannel(sc);
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
	
	private void assertOpenChannel(SocketChannel sc) throws IOException {
		if(null != sc && sc.isOpen())
			return;
		
		logger.warn(String.format("[ =>] [%s] Write closed", connectionid));
		throw new IOException("Channel closed while writing");
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
