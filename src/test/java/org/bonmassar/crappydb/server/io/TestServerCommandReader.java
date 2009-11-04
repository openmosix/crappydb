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
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactoryDelegate;
import org.bonmassar.crappydb.server.memcache.protocol.ExceptionCommand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class TestServerCommandReader {

	private ServerCommandReader cmdreader;
	private BufferReader input;
	private CommandFactoryDelegate commandFactory;
	private SelectionKey selector;
	
	@Before
	public void setUp() {
		input = mock(BufferReader.class);
		selector = mock(SelectionKey.class);
		commandFactory = mock(CommandFactoryDelegate.class);
		cmdreader = new ServerCommandReader(selector, commandFactory);
		cmdreader.inputBuffer = input;
	}
	
	@Test
	public void testReadButNothingReceived() throws IOException, CrappyDBException {		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(0, cmds.size());
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecodeGetCommandCompletedInvalidCommand() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, true);
		when(input.readTextLine()).thenReturn("gaat testkey noreply\r\n");
		ServerCommand command = new ExceptionCommand(new NotFoundException());
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(command);
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertEquals(command, cmds.get(0));
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecodeIOException() throws IOException, CrappyDBException {	
		doThrow(new IOException("Crappy read!")).when(input).precacheDataFromRemote();

		try{
			cmdreader.decodeCommands();
			fail();
		}catch(IOException ioe){
			return;
		}
	}
	
	@Test
	public void testDecodeGetCommandCompletedIOError() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, true);
		when(input.readTextLine()).thenReturn("gaat testkey noreply\r\n");
		ServerCommand command = new ExceptionCommand(new NotFoundException());
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(command);
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertEquals(command, cmds.get(0));
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecodeGetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n");
		ServerCommand command = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command);
		when(command.payloadContentLength()).thenReturn(0);
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
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
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertEquals(command1, cmds.get(0));
		assertEquals(command2, cmds.get(1));
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecode1SetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, true);
		when(input.readTextLine()).thenReturn("set testkey 888 0 20\r\n");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
				
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(22);
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertEquals(command1, cmds.get(0));
		verify(input, times(1)).precacheDataFromRemote();
		verify(command1, times(1)).addPayloadContentPart(aryEq("aaaabbbbccccddddeeee\r\n".getBytes()));
	}
	
	@Test
	public void testDecode1GetAnd1SetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n", "set testkey 888 0 20\r\n");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(0);
		ServerCommand command2 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command2);
		when(command2.payloadContentLength()).thenReturn(22);
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertEquals(command1, cmds.get(0));
		assertEquals(command2, cmds.get(1));
		verify(input, times(1)).precacheDataFromRemote();
		verify(command2, times(1)).addPayloadContentPart(aryEq("aaaabbbbccccddddeeee\r\n".getBytes()));
	}
	
	@Test
	public void testDecode1SetAnd1GetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, true);
		when(input.readTextLine()).thenReturn( "set testkey 888 0 20\r\n", "get testkey noreply\r\n");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(22);
		ServerCommand command2 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command2);
		when(command2.payloadContentLength()).thenReturn(0);
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertEquals(command1, cmds.get(0));
		assertEquals(command2, cmds.get(1));
		verify(input, times(1)).precacheDataFromRemote();
		verify(command1, times(1)).addPayloadContentPart(aryEq("aaaabbbbccccddddeeee\r\n".getBytes()));
	}
	
	@Test
	public void testDecodeGetCommand8Fragments() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, false, false, false, false, false, true);
		when(input.readTextLine()).thenReturn("g", "et ", "test", "key n", "or", "epl","y\r", "\n");
		ServerCommand command = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command);
		when(command.payloadContentLength()).thenReturn(0);
		
		for(int i = 0; i < 7; i++)
			assertEquals(0, cmdreader.decodeCommands().size());

		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertEquals(command, cmds.get(0));
		verify(input, times(8)).precacheDataFromRemote();
		verify(commandFactory, times(1)).getCommandFromCommandLine(anyString());
	}

	@Test
	public void testDecode1SetCommand8Plus4Fragments() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, false, 
				false, false, false, false, true, false, false, false, 
				false, false, false, false, false, true);
		when(input.readTextLine()).thenReturn("se","t t","e","stkey ","88","8 0 2","0\r","\n");
		when(input.getBytes(22)).thenReturn("aaaa".getBytes());
		when(input.getBytes(18)).thenReturn("bbbbcccc".getBytes());
		when(input.getBytes(10)).thenReturn("ddddeeee\r".getBytes());
		when(input.getBytes(1)).thenReturn("\n".getBytes());
				
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(22);
		
		for(int i = 0; i < 11; i++)
			assertEquals(0, cmdreader.decodeCommands().size());
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertEquals(command1, cmds.get(0));
		verify(input, times(12)).precacheDataFromRemote();
		verify(command1, times(1)).addPayloadContentPart(aryEq("aaaa".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("bbbbcccc".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("ddddeeee\r".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("\n".getBytes()));
	}
	
	@Test
	public void testDecode1SetCommand8Plus3Fragments() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, false, 
				false, false, false, false, false, false, false, false, 
				false, false, false, false, false, true);
		when(input.readTextLine()).thenReturn("se","t t","e","stkey ","88","8 0 2","0\r","\n");
		when(input.getBytes(22)).thenReturn("aaaa".getBytes());
		when(input.getBytes(18)).thenReturn("bbbbcccc".getBytes());
		when(input.getBytes(10)).thenReturn("ddddeeee\r".getBytes());
		when(input.getBytes(1)).thenReturn("\n".getBytes());
				
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(22);
		
		for(int i = 0; i < 10; i++)
			assertEquals(0, cmdreader.decodeCommands().size());
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertEquals(command1, cmds.get(0));
		verify(input, times(11)).precacheDataFromRemote();
		verify(command1, times(1)).addPayloadContentPart(aryEq("aaaa".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("bbbbcccc".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("ddddeeee\r".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("\n".getBytes()));
	}
	
	@Test
	public void testDecode1SetCommand8Plus3Fragments1Get5Fragments() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, false, 
				false, false, false, false, false, false, false, false, 
				false, false, false, false, false, true, false, false, 
				false, false, false, true);
		when(input.readTextLine()).thenReturn("se","t t","e","stkey ","88","8 0 2","0\r","\n", "get ",
				"test", "key nor", "eply\r", "\n");
		when(input.getBytes(22)).thenReturn("aaaa".getBytes());
		when(input.getBytes(18)).thenReturn("bbbbcccc".getBytes());
		when(input.getBytes(10)).thenReturn("ddddeeee\r".getBytes());
		when(input.getBytes(1)).thenReturn("\n".getBytes());
					
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(22);
		ServerCommand command2 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command2);
		when(command2.payloadContentLength()).thenReturn(0);

		for(int i = 0; i < 10; i++)
			assertEquals(0, cmdreader.decodeCommands().size());
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		
		for(int i = 0; i < 4; i++)
			assertEquals(0, cmdreader.decodeCommands().size());

		List<ServerCommand> cmds2 = cmdreader.decodeCommands();
		
		assertEquals(1, cmds.size());
		assertEquals(command1, cmds.get(0));
		verify(input, times(16)).precacheDataFromRemote();
		verify(command1, times(1)).addPayloadContentPart(aryEq("aaaa".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("bbbbcccc".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("ddddeeee\r".getBytes()));
		verify(command1, times(1)).addPayloadContentPart(aryEq("\n".getBytes()));
		
		assertEquals(1, cmds2.size());
		assertEquals(command2, cmds2.get(0));
	}
	
	@Test
	public void testCrappyDataBetweenCommands() throws IOException{
		when(input.noDataAvailable()).thenReturn(false, false, false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n     ", "     set testkey 888 0 20\r\n      ");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		ServerCommand command1 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("get testkey noreply")).thenReturn(command1);
		when(command1.payloadContentLength()).thenReturn(0);
		ServerCommand command2 = mock(ServerCommand.class);
		when(commandFactory.getCommandFromCommandLine("set testkey 888 0 20")).thenReturn(command2);
		when(command2.payloadContentLength()).thenReturn(22);
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertEquals(command1, cmds.get(0));
		assertEquals(command2, cmds.get(1));
		verify(input, times(1)).precacheDataFromRemote();
		verify(command2, times(1)).addPayloadContentPart(aryEq("aaaabbbbccccddddeeee\r\n".getBytes()));

	}
}
