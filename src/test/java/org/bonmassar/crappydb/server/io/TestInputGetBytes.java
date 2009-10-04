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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.junit.Before;
import org.junit.Test;

public class TestInputGetBytes {

	private SocketChannel socketchannel;
	private InputPipeMock input;
	private SelectionKey fakeSelector;
	
	@Before
	public void setUp() throws ParseException {
		Configuration.INSTANCE.parse(null);
		fakeSelector = mock(SelectionKey.class);
		socketchannel = mock(SocketChannel.class);
		input = new InputPipeMock(fakeSelector);
		input.openChannel();
		when(fakeSelector.channel()).thenReturn(socketchannel);
	}

	@Test
	public void testReadZeroBytes() throws IOException {
		input.addChunk("\0\0\0\0".getBytes());
		input.precacheDataFromRemote();
		byte[] result = input.getBytes(0);
		assertNotNull(result);
		assertEquals(0, result.length);
		assertFalse(input.noDataAvailable());
	}
	
	@Test
	public void testReadWhenEmpty() throws IOException {
		input.addChunk("\0\0\0\0".getBytes());
		input.precacheDataFromRemote();
		byte[] result = input.getBytes(4);
		assertNotNull(result);
		assertEquals("\0\0\0\0", new String(result));
		result = input.getBytes(4);
		assertEquals(0, result.length);
		assertTrue(input.noDataAvailable());
	}
	
	@Test
	public void testReadMoreBytesThanAvailables() throws IOException {
		input.addChunk("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0".getBytes());
		input.precacheDataFromRemote();
		byte[] result = input.getBytes(33);
		assertNotNull(result);
		assertEquals(32, result.length);
		assertEquals("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",new String(result) );
		assertTrue(input.noDataAvailable());
	}
	
	@Test
	public void testReadLessBytesThanAvailables() throws IOException {
		input.addChunk("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0".getBytes());
		input.precacheDataFromRemote();
		byte[] result = input.getBytes(31);
		assertNotNull(result);
		assertEquals(31, result.length);
		assertEquals("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",new String(result) );
		assertFalse(input.noDataAvailable());
	}
	
	@Test
	public void testReadSameBytesThanAvailables() throws IOException {
		input.addChunk("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0".getBytes());
		input.precacheDataFromRemote();
		byte[] result = input.getBytes(32);
		assertNotNull(result);
		assertEquals(32, result.length);
		assertEquals("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",new String(result) );
		assertTrue(input.noDataAvailable());
	}
	
	@Test
	public void testReadTwoLinesAndSomeData() throws IOException {
		input.addChunk("taralilalla\r\nparalilalla\r\n\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0".getBytes());
		input.precacheDataFromRemote();
		assertEquals("taralilalla\r\n", input.readTextLine());
		assertEquals("paralilalla\r\n", input.readTextLine());
		byte[] result = input.getBytes(32);
		assertNotNull(result);
		assertEquals(32, result.length);
		assertEquals("\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0",new String(result) );
		assertTrue(input.noDataAvailable());
	}
	
	@Test
	public void testMultipleReads() throws IOException {
		input.addChunk("taralilalla\r\nparalilalla\r\n".getBytes());
		input.precacheDataFromRemote();
		assertEquals("taralilall", new String(input.getBytes( 10 )));
		assertEquals("a\r\nparal", new String(input.getBytes( 8 )));
		assertEquals("ila", new String(input.getBytes( 3 )));
		assertEquals("lla\r", new String(input.getBytes( 4 )));
		
		assertFalse(input.noDataAvailable());
	}
}
