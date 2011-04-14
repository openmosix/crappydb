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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.mockito.Mockito;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestUdpCommandWriter {

	private UdpCommandWriter writer;
	private SelectionKey key;
	private SocketAddress client;
	private DatagramChannel channel;
	
	@Before
	public void setUp() {
		client = Mockito.mock(SocketAddress.class);
		channel = Mockito.mock(DatagramChannel.class);
		key = Mockito.mock(SelectionKey.class);
		writer = new UdpCommandWriter(key);
	}
	
	@Test
	public void testWriteNoClient() throws IOException {
		writer.setClient(channel, null, '@');
		writer.write();
		Mockito.verify(channel, Mockito.times(0)).send((ByteBuffer) Mockito.anyObject(), (SocketAddress)Matchers.isNull());
	}
	
	@Test
	public void testWriteNoChannel() throws IOException {
		writer.setClient(null, client, '@');
		writer.write();
		Mockito.verify(channel, Mockito.times(0)).send((ByteBuffer) Mockito.anyObject(), (SocketAddress)Matchers.anyObject());
	}
	
	@Test
	public void testWriteNoDataToFlush() throws IOException {
		writer.setClient(channel, client, '@');
		writer.write();
		Mockito.verify(channel, Mockito.times(0)).send((ByteBuffer) Mockito.anyObject(), (SocketAddress)Matchers.anyObject());
	}
	
	@Test
	public void testWriteOneDataToFlush() throws IOException {
		writer.setClient(channel, client, '@');
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put("all your bases are belong to us.".getBytes());
		writer.addToQueue(buffer);
		Mockito.when(channel.send((ByteBuffer) Mockito.anyObject(), (SocketAddress)Matchers.anyObject())).thenAnswer(new Answer<Integer>(){

			public Integer answer(InvocationOnMock invocation) throws Throwable {
				ByteBuffer buffer = (ByteBuffer)invocation.getArguments()[0];
				SocketAddress address = (SocketAddress) invocation.getArguments()[1];
				assertEquals('@',buffer.getChar());
				assertEquals((char)0x0000,buffer.getChar());
				assertEquals((char)0x0001,buffer.getChar());
				assertEquals((char)0x0000,buffer.getChar());
				byte[] data = new byte[32];
				buffer.get(data);
				assertEquals("all your bases are belong to us.", new String(data));
				assertEquals(client, address);
				return null;
			}
			
		});
		writer.write();
	}
	
	@Test
	public void testWriteMultipleDataToFlush() throws IOException {
		writer.setClient(channel, client, '@');
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put("all your bases are belong to us.".getBytes());
		writer.addToQueue(buffer);
		ByteBuffer buffer2 = ByteBuffer.allocate(1024);
		buffer2.put("deadbeef".getBytes());
		writer.addToQueue(buffer2);
		ByteBuffer buffer3 = ByteBuffer.allocate(1024);
		buffer3.put("piccipiccipiccipicci".getBytes());
		writer.addToQueue(buffer3);
		Mockito.when(channel.send((ByteBuffer) Mockito.anyObject(), (SocketAddress)Matchers.anyObject())).thenAnswer(new Answer<Integer>(){

			private int counter = 0;
			
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				ByteBuffer buffer = (ByteBuffer)invocation.getArguments()[0];
				SocketAddress address = (SocketAddress) invocation.getArguments()[1];
				if(0 == counter){
					assertEquals('@',buffer.getChar());
					assertEquals((char)0x0000,buffer.getChar());
					assertEquals((char)0x0003,buffer.getChar());
					assertEquals((char)0x0000,buffer.getChar());
					byte[] data = new byte[32];
					buffer.get(data);
					assertEquals("all your bases are belong to us.", new String(data));
				}else if(1 == counter){
					assertEquals('@',buffer.getChar());
					assertEquals((char)0x0001,buffer.getChar());
					assertEquals((char)0x0003,buffer.getChar());
					assertEquals((char)0x0000,buffer.getChar());
					byte[] data = new byte[8];
					buffer.get(data);
					assertEquals("deadbeef", new String(data));					
				}else if(2 == counter){
					assertEquals('@',buffer.getChar());
					assertEquals((char)0x0002,buffer.getChar());
					assertEquals((char)0x0003,buffer.getChar());
					assertEquals((char)0x0000,buffer.getChar());
					byte[] data = new byte[20];
					buffer.get(data);
					assertEquals("piccipiccipiccipicci", new String(data));
				}
				counter++;
				assertEquals(client, address);
				return null;
			}
			
		});
		writer.write();
	}
}
