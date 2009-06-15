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
import java.util.List;

import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class TestServerCommandReader {

	private ServerCommandReader cmdreader;
	private InputPipe input;
	private CommandFactory commandFactory;
	private SelectionKey selector;
	
	@Before
	public void setUp() {
		input = mock(InputPipe.class);
		selector = mock(SelectionKey.class);
		commandFactory = mock(CommandFactory.class);
		cmdreader = new ServerCommandReader(selector, commandFactory);
		cmdreader.input = input;
	}
	
	@Test
	public void testReadButNothingReceived() throws IOException, CrappyDBException {		
		List<ServerCommand> cmds = cmdreader.decodeCommand();
		assertEquals(0, cmds.size());
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecodeGetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n");
		ServerCommand command = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command);
		when(command.payloadContentLength()).thenReturn(0);
		
		List<ServerCommand> cmds = cmdreader.decodeCommand();
		assertEquals(1, cmds.size());
		assertEquals(command, cmds.get(0));
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecode2GetCommandsCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n", "get testotherkey noreply\r\n");
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(0);
		ServerCommand command2 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testotherkey noreply")).thenReturn(command2);
		when(command2.payloadContentLength()).thenReturn(0);
		
		List<ServerCommand> cmds = cmdreader.decodeCommand();
		assertEquals(2, cmds.size());
		assertEquals(command1, cmds.get(0));
		assertEquals(command2, cmds.get(1));
		verify(input, times(1)).precacheDataFromRemote();
	}
	
}
