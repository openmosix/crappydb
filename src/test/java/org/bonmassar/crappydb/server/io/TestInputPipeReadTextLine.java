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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;

public class TestInputPipeReadTextLine {

	private SocketChannel socketchannel;
	private InputPipeMock input;
	private SelectionKey fakeSelector;
	
	@Before
	public void setUp() {
		fakeSelector = mock(SelectionKey.class);
		socketchannel = mock(SocketChannel.class);
		input = new InputPipeMock(fakeSelector);
		input.openChannel();
		when(fakeSelector.channel()).thenReturn(socketchannel);
	}

	@Test
	public void testReadOneLine() throws IOException {
		input.addChunk("ma che bel castello marcondirondirondello!\r\n".getBytes());
		input.precacheDataFromRemote();
		assertEquals("ma che bel castello marcondirondirondello!\r\n", input.readTextLine());
		assertTrue(input.noDataAvailable());
		assertEquals("", input.getRemainingDataAsText());
	}
	
	@Test
	public void testReadOneLineAndGarbage() throws IOException {
		input.addChunk("ma che bel castello marcondirondirondello!\r\nMiaoMiaoMiaoooooo".getBytes());
		input.precacheDataFromRemote();
		assertEquals("ma che bel castello marcondirondirondello!\r\n", input.readTextLine());
		assertFalse(input.noDataAvailable());
		assertEquals("MiaoMiaoMiaoooooo", input.getRemainingDataAsText());
	}
	
	@Test
	public void testReadOneLineDoubleCarriageReturn() throws IOException {
		input.addChunk("ma che bel castello marcondirondirondello!\r\nMiaoMiaoMiaoooooo\r\n".getBytes());
		input.precacheDataFromRemote();
		assertEquals("ma che bel castello marcondirondirondello!\r\n", input.readTextLine());
		assertFalse(input.noDataAvailable());
		assertEquals("MiaoMiaoMiaoooooo\r\n", input.getRemainingDataAsText());
	}
	
	@Test
	public void testReadTwoLines() throws IOException {
		input.addChunk("ma che bel castello marcondirondirondello!\r\nMiaoMiaoMiaoooooo\r\n".getBytes());
		input.precacheDataFromRemote();
		assertEquals("ma che bel castello marcondirondirondello!\r\n", input.readTextLine());
		assertFalse(input.noDataAvailable());
		assertEquals("MiaoMiaoMiaoooooo\r\n", input.readTextLine());
		assertTrue(input.noDataAvailable());
		assertEquals("", input.getRemainingDataAsText());
	}
	
	@Test
	public void testReadTwoLinesAndGarbage() throws IOException {
		input.addChunk("ma che bel castello marcondirondirondello!\r\nMiaoMiaoMiaoooooo\r\nCippicippicippi".getBytes());
		input.precacheDataFromRemote();
		assertEquals("ma che bel castello marcondirondirondello!\r\n", input.readTextLine());
		assertFalse(input.noDataAvailable());
		assertEquals("MiaoMiaoMiaoooooo\r\n", input.readTextLine());
	}
	
}
