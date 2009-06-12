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

	private static final int maxChunkSize = 8 * 1024;
	private SelectionKey requestsDescriptor;
	private ByteBuffer buffer;
	private Logger logger = Logger.getLogger(InputPipe.class);
	
	public InputPipe(SelectionKey requestsDescriptor){
		this.requestsDescriptor = requestsDescriptor;
		buffer = ByteBuffer.allocate(InputPipe.maxChunkSize);
	}
	
	public void getDataFromRemote() throws IOException{
		SocketChannel channel = (SocketChannel) requestsDescriptor.channel();
		if (!channel.isOpen())
			throw new IOException("Read descriptor is closed");

		getReceivedData(channel);
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

}
