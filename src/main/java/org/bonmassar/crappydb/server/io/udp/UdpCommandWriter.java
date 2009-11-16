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
import java.nio.channels.SelectionKey;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.io.ServerCommandWriter;

public class UdpCommandWriter extends ServerCommandWriter {

	private SocketAddress client;
	private DatagramChannel channel;
	private char reqId;
	private static final Logger logger = Logger.getLogger(UdpCommandWriter.class);
	
	public UdpCommandWriter(SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
	}

	@Override
	public void write() throws IOException {
		if(null == client || null == channel){
			logger.warn("Client or udp channel is null, no response.");
			return;
		}
		
		synchronized(bufferList){
			if(bufferList.isEmpty())
				return;
						
			char packetNo = 0;
			char totPackets = (char) bufferList.size();
			for (ByteBuffer buffer : bufferList) {
				ByteBuffer bufferWithHeader = ByteBuffer.allocateDirect(buffer.remaining()+8); 
//				+----+----+----+----+
//			    |  Req Id | Pac Num |  
//			    | HI | LW | HI | LW |  
//			    +----+----+----+----+
//			    | Pac Tot | Reserv. |
//			    | HI | LW | HI | LW |  
//				+----+----+----+----+
				bufferWithHeader.putChar(reqId);
				bufferWithHeader.putChar(packetNo++);
				bufferWithHeader.putChar(totPackets);
				bufferWithHeader.putChar((char)0x0000);
				
				bufferWithHeader.put(buffer.array(), 0, buffer.remaining()-1);
				bufferWithHeader.flip();
				channel.send(bufferWithHeader, client);
			}
		}		
	}

	public void setClient(DatagramChannel channel, SocketAddress client, char reqId) {
		this.client = client;
		this.channel = channel;
		this.reqId = reqId;
	}

	protected void addToQueue(ByteBuffer bb) {
		synchronized(bufferList){
			bufferList.addLast(bb);
		}
	}
}
