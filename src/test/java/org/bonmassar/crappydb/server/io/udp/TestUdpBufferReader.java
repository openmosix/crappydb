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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestUdpBufferReader {

	private UdpBufferReader udpBuffer;
	private UdpCommandWriter writer;
	private SelectionKey key;
	
	@Before
	public void setUp() {
		key = Mockito.mock(SelectionKey.class);
		writer = Mockito.mock(UdpCommandWriter.class);
		udpBuffer = new UdpBufferReader(writer, key);
	}
	
	@Test
	public void testNotUdpChannel() throws IOException {
		try{
			udpBuffer.channelRead(null);
		}catch(IllegalArgumentException npe){
			return;
		}
		fail();
	}
	
	@Test
	public void testNotDatagramChannel() throws IOException {
		try{
			udpBuffer.channelRead(SocketChannel.open());
		}catch(IllegalArgumentException npe){
			return;
		}
		fail();
	}
	
	@Test
	public void testReceiveSomeData() throws IOException {
		DatagramChannel channel = Mockito.mock(DatagramChannel.class);
		final SocketAddress client = Mockito.mock(SocketAddress.class);
		Mockito.when(channel.receive((ByteBuffer) Matchers.anyObject())).thenAnswer(new Answer<SocketAddress>(){

			public SocketAddress answer(InvocationOnMock invocation)
					throws Throwable {
				ByteBuffer buffer = (ByteBuffer) invocation.getArguments()[0];
				buffer.put((byte)(0x00));
				buffer.put((byte)(0x40));
				buffer.put((byte)(0x00));
				buffer.put((byte)(0x00));
				buffer.put((byte)(0x00));
				buffer.put((byte)(0x00));
				buffer.put((byte)(0x00));
				buffer.put((byte)(0x00));
				buffer.put("get terminenzio 12 145 48\r\n".getBytes());
				return client;
			}
		});
		
		assertEquals(27, udpBuffer.channelRead(channel));
		Mockito.verify(writer, Mockito.times(1) ).setClient(channel, client, '@');
	}
}
