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
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;

import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.io.BufferReader;

public class TcpBufferReader extends BufferReader {

	public TcpBufferReader(SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
		buffer = ByteBuffer.allocate(Configuration.INSTANCE.getBufferSize());
	}

	protected int channelRead(ReadableByteChannel channel) throws IOException {
		try{
			return channel.read(buffer);
		}catch(java.nio.BufferOverflowException boe){
			logger.fatal(String.format("[<= ] [%s] Buffer overflow writing data into chunk buffer", connectionid), boe);
			throw new IOException("Chunk too large");
		}
	}
}
