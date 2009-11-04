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
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bonmassar.crappydb.server.memcache.protocol.CommandFactoryDelegate;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestEstablishedConnection extends TestCase {

	private TransportSession conn;
	private CommandFactoryDelegate factory;
	private SelectionKey selector;
	
	class MockEstablishedConnection extends TransportSession {
		
		public MockEstablishedConnection(SelectionKey selector,
				CommandFactoryDelegate commandFactory) {
			super(selector, commandFactory);
		}

		@Override
		protected void init(SelectionKey selector, CommandFactoryDelegate commandFactory) {
			commandWriter = mock(ServerCommandWriter.class);
			commandReader = mock(ServerCommandReader.class);
			commandCloser = mock(ServerCommandCloser.class);
		}
	}
	
	@Before
	public void setUp() {
		selector = mock(SelectionKey.class);
		factory = mock(CommandFactoryDelegate.class);
		conn = new MockEstablishedConnection(selector, factory);
	}
	
	@Test
	public void testDoReadNothingToRead() throws IOException{
		when(conn.commandReader.decodeCommands()).thenReturn(new ArrayList<ServerCommand>());
		
		List<ServerCommand> result = conn.doRead();
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(conn.commandCloser, times(0)).closeConnection();
	}
	
	@Test
	public void testDoReadIOError() throws IOException {
		when(conn.commandReader.decodeCommands()).thenThrow(new IOException("BOOM!"));
		
		List<ServerCommand> result = conn.doRead();
		assertNull(result);
		verify(conn.commandCloser, times(1)).closeConnection();
	}
	
	@Test
	public void testDoReadManyCommands() throws IOException {
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		ServerCommand cmd3 = mock(ServerCommand.class);
		when(conn.commandReader.decodeCommands()).thenReturn(Arrays.asList(cmd1, cmd2, cmd3));
		
		List<ServerCommand> result = conn.doRead();
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(cmd1, result.get(0));
		assertEquals(cmd2, result.get(1));
		assertEquals(cmd3, result.get(2));
		verify(cmd1, times(1)).attachCommandWriter(conn.commandWriter);
		verify(cmd2, times(1)).attachCommandWriter(conn.commandWriter);
		verify(cmd3, times(1)).attachCommandWriter(conn.commandWriter);
		verify(conn.commandCloser, times(0)).closeConnection();
	}
	
	@Test
	public void testDoReadOneCommand() throws IOException {
		ServerCommand cmd = mock(ServerCommand.class);
		when(conn.commandReader.decodeCommands()).thenReturn(Arrays.asList(cmd));
		
		List<ServerCommand> result = conn.doRead();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(cmd, result.get(0));
		verify(cmd, times(1)).attachCommandWriter(conn.commandWriter);
		verify(conn.commandCloser, times(0)).closeConnection();
	}
	
	@Test
	public void testDoWrite() throws IOException {
		conn.doWrite();
		
		verify(conn.commandWriter, times(1)).write();
		verify(conn.commandCloser, times(0)).closeConnection();
	}
	
	@Test
	public void testDoWriteIOError() throws IOException {
		doThrow(new IOException("BOOM!")).when(conn.commandWriter).write();
		conn.doWrite();
		
		verify(conn.commandWriter, times(1)).write();
		verify(conn.commandCloser, times(1)).closeConnection();
	}
	
}
