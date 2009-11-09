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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.junit.Before;
import org.junit.Test;

public class TestTcpBufferReaderPrecacheDataFromRemote {

	private SocketChannel socketchannel;
	private TcpBufferReaderMock input;
	private SelectionKey fakeSelector;
	
	@Before
	public void setUp() throws ParseException {
		Configuration.INSTANCE.parse(null);
		fakeSelector = mock(SelectionKey.class);
		socketchannel = mock(SocketChannel.class);
		input = new TcpBufferReaderMock(fakeSelector);
	}
	
	@Test
	public void testChannelIsNull() {
		when(fakeSelector.channel()).thenReturn(null);
		
		try{
			input.precacheDataFromRemote();
			fail();
		}
		catch(IOException ioe){
			assertTrue(input.noDataAvailable());
			return;
		}
	}
	
	@Test
	public void testChannelIsClosed() {
		when(fakeSelector.channel()).thenReturn(socketchannel);
		
		try{
			input.precacheDataFromRemote();
			fail();
		}
		catch(IOException ioe){
			assertTrue(input.noDataAvailable());
			return;
		}
	}
	
	@Test
	public void testErrorWhileReading() {
		input.openChannel();
		when(fakeSelector.channel()).thenReturn(socketchannel);
		try {
			input.precacheDataFromRemote();
			fail();
		} catch (IOException e) {
			assertTrue(input.noDataAvailable());
			return;
		}
	}

	
	@Test
	public void testReadWithNoData() throws IOException {
		input.openChannel();
		input.addChunk("".getBytes());
		when(fakeSelector.channel()).thenReturn(socketchannel);
		
		input.precacheDataFromRemote();
		assertTrue(input.noDataAvailable());
	}
	
	@Test
	public void testReadWith1Char() throws IOException {
		input.openChannel();
		input.addChunk("A".getBytes());

		when(fakeSelector.channel()).thenReturn(socketchannel);
		input.precacheDataFromRemote();
		assertFalse(input.noDataAvailable());
		
		assertTrue(input.getBuffer().hasRemaining());
		assertEquals(1, input.getBuffer().remaining());
		assertEquals('A', (char)input.getBuffer().get());
	}
	
	@Test
	public void testReadWith1String() throws IOException {
		input.openChannel();
		input.addChunk("This is a string".getBytes());

		when(fakeSelector.channel()).thenReturn(socketchannel);
		input.precacheDataFromRemote();
		
		assertTrue(input.getBuffer().hasRemaining());
		assertFalse(input.noDataAvailable());
		assertEquals(16, input.getBuffer().remaining());
		byte[] result = new byte[16];
		input.getBuffer().get(result);
		assertEquals("This is a string", new String(result));
	}
	
	@Test
	public void testReadWith3String() throws IOException {
		input.openChannel();
		input.addChunk("This is a st".getBytes());
		input.addChunk("ring that sh".getBytes());
		input.addChunk( "ould be concatenated\r\n".getBytes());

		when(fakeSelector.channel()).thenReturn(socketchannel);
		input.precacheDataFromRemote();
		input.precacheDataFromRemote();
		input.precacheDataFromRemote();
		
		assertTrue(input.getBuffer().hasRemaining());
		assertFalse(input.noDataAvailable());
		assertEquals(22, input.getBuffer().remaining());
		byte[] result = new byte[22];
		input.getBuffer().get(result);
		assertEquals("ould be concatenated\r\n", new String(result));
	}
	
	@Test
	public void testReadWithLongLongChunk() throws IOException {
		input.openChannel();
		input.addChunk(longLongText().getBytes());

		when(fakeSelector.channel()).thenReturn(socketchannel);
		input.precacheDataFromRemote();
		
		assertTrue(input.getBuffer().hasRemaining());
		assertEquals(Configuration.INSTANCE.getBufferSize(), input.getBuffer().remaining());
		assertFalse(input.noDataAvailable());
		byte[] result = new byte[Configuration.INSTANCE.getBufferSize()];
		input.getBuffer().get(result);
		assertEquals(new String(longLongText()), new String(result));
	}
	
	@Test
	public void testReadWithLongLongChunkPlusOneChar() throws IOException {
		input.openChannel();
		input.addChunk((longLongText()+"A").getBytes());

		when(fakeSelector.channel()).thenReturn(socketchannel);
		try{
			input.precacheDataFromRemote();
			fail();
		}catch(IOException ioe){
			assertEquals("Chunk too large", ioe.getMessage());
			assertTrue(input.noDataAvailable());
		}
	}
	
	
	private String longLongText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Configuration.INSTANCE.getBufferSize(); i++){
			sb.append("A");
		}
		return sb.toString();
		
	}
	
	
}