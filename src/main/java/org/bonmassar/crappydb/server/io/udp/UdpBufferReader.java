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

package org.bonmassar.crappydb.server.io.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;

import org.bonmassar.crappydb.server.io.BufferReader;

public class UdpBufferReader extends BufferReader {

	private static int maxUDPPacketSize = 65507;
	private final UdpCommandWriter writer;
	
	public UdpBufferReader(UdpCommandWriter writer, SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
		this.writer = writer;
		buffer = ByteBuffer.allocateDirect(maxUDPPacketSize);
	}

	@Override
	protected int channelRead(ReadableByteChannel channel) throws IOException {
		if(!(channel instanceof DatagramChannel))
			throw new IllegalArgumentException("Channel is not a datagram channel");
		
		DatagramChannel UDPChannel = ((DatagramChannel)channel);
		SocketAddress client = UDPChannel.receive(buffer);
		int nbytes = buffer.position();
		if(nbytes > 8){
			buffer.flip();
			char reqId = buffer.getChar();
			buffer.getChar();
			buffer.getInt();
			writer.setClient(UDPChannel, client, reqId);
			return nbytes-8;
		}
		return 0;
	}

}
