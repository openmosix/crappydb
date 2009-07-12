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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import org.bonmassar.crappydb.mocks.OutputChannelMock;
import org.bonmassar.crappydb.mocks.WhateverChannel;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestCommandWriter extends TestCase {
	
	private ServerCommandWriter writer;
	private SelectionKey selector;
	private WhateverChannel channel;
	private OutputChannelMock dataTransfer;
	
	private String[] text = {"There is no need to send any command to end the session. A client may\r\n",
			"just close the connection at any moment it no longer needs it. Note,\r\n",
			"however, that clients are encouraged to cache their connections rather\r\n",
			"than reopen them every time they need to store or retrieve data.  This\r\n",
			"is because memcached is especially designed to work very efficiently\r\n",
			"with a very large number (many hundreds, more than a thousand if\r\n",
			"necessary) of open connections. Caching connections will eliminate the\r\n",
			"overhead associated with establishing a TCP connection (the overhead\r\n",
			"of preparing for a new connection on the server side is insignificant\r\n",
			"compared to this).\r\n"};
	
	@Before
	public void setUp() {
		selector = mock(SelectionKey.class);
		writer = new ServerCommandWriterMock(selector);
		channel = mock(WhateverChannel.class);
		dataTransfer = new OutputChannelMock();
	}
	
	@Test
	public void testNoop() {
		writer.writeToOutstanding("");
		assertEquals(0, writer.bufferList.size());
	}
	
	@Test
	public void testNull() {
		writer.writeToOutstanding((byte[])null);
		assertEquals(0, writer.bufferList.size());
	}
		
	@Test
	public void testWriteOutstanding() {
		writer.writeToOutstanding("ma che bel castello marcondirondirondello!!!\n\r\n\r\n");
		assertEquals(1, writer.bufferList.size());
		ByteBuffer b = writer.bufferList.get(0);
		assertNotNull(b);
		assertEquals("ma che bel castello marcondirondirondello!!!\n\r\n\r\n", getDataFromBuffer(b, 49));
		verify(selector , times(1)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}
		
	@Test
	public void testFlushAllOnSocketIOError() {
		stubClosedSocket();
		when( selector.channel() ).thenReturn(channel);

		writer.writeToOutstanding("ma che bel castello marcondirondirondello!!!\n\r\n\r\n");
				
		try {
			writer.write();
		} catch (IOException e) {
			verify(selector , times(1)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			return;
		}
		fail();
	}
	
	@Test
	public void testFlushButNoData() throws IOException {
		when( selector.channel() ).thenReturn(channel);
		
		writer.write();	//If does not blow that's ok
	}

	@Test
	public void testFlushAllOneShot() throws IOException {
		writer.writeToOutstanding("ma che bel castello marcondirondirondello!!!\n\r\n\r\n");
		
		when( selector.channel() ).thenReturn(channel);
		dataTransfer.transferInChunks(new Integer[]{49});
		
		when( channel.write((ByteBuffer) anyObject()) ).thenAnswer(dataTransfer);
		
		writer.write();	//If does not blow that's ok
		assertTrue(dataTransfer.dataTransfered(new String[]{"ma che bel castello marcondirondirondello!!!\n\r\n\r\n"}));
		verify( channel, times(1)).write((ByteBuffer) anyObject());
		verify(selector , times(1)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		verify( selector, times(1) ).interestOps(SelectionKey.OP_READ);		
	}
	

	@Test
	public void testFlushOneMultipleShots() throws IOException {
		writer.writeToOutstanding("ma che bel castello marcondirondirondello!!!\n\r\n\r\n");
		
		when( selector.channel() ).thenReturn(channel);
		dataTransfer.transferInChunks(new Integer[]{10, 5, 5, 15, 13, 1});
		
		when( channel.write((ByteBuffer) anyObject()) ).thenAnswer(dataTransfer);
		
		for(int i = 0; i < 6; i++){
			writer.write();
		}
		
		assertTrue(dataTransfer.dataTransfered(new String[]{"ma che bel",
				" cast","ello ","marcondirondiro", "ndello!!!\n\r\n\r", "\n"}));
		
		verify( channel, times(6)).write((ByteBuffer) anyObject());
		verify( selector , times(6)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		verify( selector, times(6) ).interestOps(SelectionKey.OP_READ);		

	}
	
	@Test
	public void testFlushMultipleInOneShot() throws IOException {
		writeAText();
		
		when( selector.channel() ).thenReturn(channel);
		dataTransfer.transferInChunks(new Integer[]{71,70,72,72,70,66,72,70, 71, 20});
		
		when( channel.write((ByteBuffer) anyObject()) ).thenAnswer(dataTransfer);
		
		writer.write();
		
		assertTrue(dataTransfer.dataTransfered(new String[]{text[0], text[1], text[2], 
				text[3], text[4], text[5], text[6], text[7], text[8], text[9]}));
		
		verify( channel, times(10)).write((ByteBuffer) anyObject());
		verify( selector , times(10)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		verify( selector, times(1) ).interestOps(SelectionKey.OP_READ);		
	}

	@Test
	public void testFlushMultipleInMultipleShot() throws IOException {
		writeAText();
		
		when( selector.channel() ).thenReturn(channel);
		dataTransfer.transferInChunks(new Integer[]{35, 36, 70, 22, 50, 72, 40, 30, 66, 1, 71, 70, 71, 15, 5});
		
		when( channel.write((ByteBuffer) anyObject()) ).thenAnswer(dataTransfer);
		
		for(int i = 0; i < 6; i++)
			writer.write();
		
		assertTrue(dataTransfer.dataTransfered(new String[]{
				text[0].substring(0, 35), //chunk 1
				text[0].substring(35), text[1], text[2].substring(0, 22), 
				text[2].substring(22), text[3], text[4].substring(0, 40),
				text[4].substring(40), text[5], text[6].substring(0, 1), 
				text[6].substring(1), text[7], text[8], text[9].substring(0, 15), 
				text[9].substring(15)}));//chunk 6
		
		verify( channel, times(15)).write((ByteBuffer) anyObject());
		verify( selector , times(15)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		verify( selector, times(6) ).interestOps(SelectionKey.OP_READ);		
	}

	
	@Test
	public void testWriteText() {
		writeAText();
		assertEquals(10, writer.bufferList.size());
		assertIHaveTheText();
		verify(selector , times(10)).interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
	}

	private String getDataFromBuffer(ByteBuffer buffer, int readLen) {
		byte[] tmp = new byte[readLen];
		buffer.get(tmp);
		return new String(tmp);
	}
	
	private void assertIHaveTheText() {
		assertEquals(text[0], getDataFromBuffer(writer.bufferList.get(0), 71));		
		assertEquals(text[1], getDataFromBuffer(writer.bufferList.get(1), 70));		
		assertEquals(text[2], getDataFromBuffer(writer.bufferList.get(2), 72));		
		assertEquals(text[3], getDataFromBuffer(writer.bufferList.get(3), 72));		
		assertEquals(text[4], getDataFromBuffer(writer.bufferList.get(4), 70));		
		assertEquals(text[5], getDataFromBuffer(writer.bufferList.get(5), 66));		
		assertEquals(text[6], getDataFromBuffer(writer.bufferList.get(6), 72));		
		assertEquals(text[7], getDataFromBuffer(writer.bufferList.get(7), 70));		
		assertEquals(text[8], getDataFromBuffer(writer.bufferList.get(8), 71));		
		assertEquals(text[9], getDataFromBuffer(writer.bufferList.get(9), 20));		
	}
	
	private void writeAText() {
		writer.writeToOutstanding(text[0]);
		writer.writeToOutstanding(text[1]);
		writer.writeToOutstanding(text[2]);
		writer.writeToOutstanding(text[3]);
		writer.writeToOutstanding(text[4]);
		writer.writeToOutstanding(text[5]);
		writer.writeToOutstanding(text[6]);
		writer.writeToOutstanding(text[7]);
		writer.writeToOutstanding(text[8]);
		writer.writeToOutstanding(text[9]);
	}
	
	private void stubClosedSocket(){
		((ServerCommandWriterMock)writer).blowOnSocketClosed();
	}
	
}
