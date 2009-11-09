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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.exceptions.ClosedConnectionException;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ExceptionCommand;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestServerCommandReader {

	private CommandResponse resp;
	private ServerCommandReader cmdreader;
	private BufferReader input;
	private StorageAccessLayer sal;
	
	@Before
	public void setUp() throws ParseException {
		input = mock(BufferReader.class);
		cmdreader = new ServerCommandReader(input);
		cmdreader.inputBuffer = input;
		resp = mock(CommandResponse.class);
		sal = mock(StorageAccessLayer.class);
		CommandFactory.INSTANCE.setStorageLayer(sal);
		Configuration.INSTANCE.parse(null);
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
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertTrue((cmds.get(0) instanceof ExceptionCommand));
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
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertTrue((cmds.get(0) instanceof ExceptionCommand));
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	
	@Test
	public void testDecodeGetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n");
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertNotNull(cmds.get(0));
		assertFalse((cmds.get(0) instanceof ExceptionCommand));

		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecode2GetCommandsCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n", "get testotherkey noreply\r\n");
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertNotNull(cmds.get(0));
		assertFalse((cmds.get(0) instanceof ExceptionCommand));

		assertNotNull(cmds.get(1));
		assertFalse((cmds.get(1) instanceof ExceptionCommand));

		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecode1SetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, true);
		when(input.readTextLine()).thenReturn("set testkey 888 0 20\r\n");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		cmds.get(0).attachCommandWriter(resp);
		cmds.get(0).execCommand();
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecode1GetAnd1SetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n", "set testkey 888 0 20\r\n");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		assertFalse((cmds.get(1) instanceof ExceptionCommand));
		cmds.get(1).attachCommandWriter(resp);
		cmds.get(1).execCommand();

		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecode1SetAnd1GetCommandCompleted() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, true);
		when(input.readTextLine()).thenReturn( "set testkey 888 0 20\r\n", "get testkey noreply\r\n");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		assertFalse((cmds.get(1) instanceof ExceptionCommand));
		cmds.get(0).attachCommandWriter(resp);
		cmds.get(0).execCommand();
		verify(input, times(1)).precacheDataFromRemote();
	}
	
	@Test
	public void testDecodeGetCommand8Fragments() throws IOException, CrappyDBException {	
		when(input.noDataAvailable()).thenReturn(false, false, false, false, false, false, false, false, true);
		when(input.readTextLine()).thenReturn("g", "et ", "test", "key n", "or", "epl","y\r", "\n");
		
		for(int i = 0; i < 7; i++)
			assertEquals(0, cmdreader.decodeCommands().size());

		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		verify(input, times(8)).precacheDataFromRemote();
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
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
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});
		
		for(int i = 0; i < 11; i++)
			assertEquals(0, cmdreader.decodeCommands().size());
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		cmds.get(0).attachCommandWriter(resp);
		cmds.get(0).execCommand();
		verify(input, times(12)).precacheDataFromRemote();
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
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});

		for(int i = 0; i < 10; i++)
			assertEquals(0, cmdreader.decodeCommands().size());
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(1, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		cmds.get(0).attachCommandWriter(resp);
		cmds.get(0).execCommand();
		verify(input, times(11)).precacheDataFromRemote();
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
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});
		
		for(int i = 0; i < 10; i++)
			assertEquals(0, cmdreader.decodeCommands().size());
				
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		
		for(int i = 0; i < 4; i++)
			assertEquals(0, cmdreader.decodeCommands().size());

		List<ServerCommand> cmds2 = cmdreader.decodeCommands();
		
		assertEquals(1, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		cmds.get(0).attachCommandWriter(resp);
		cmds.get(0).execCommand();

		verify(input, times(16)).precacheDataFromRemote();
		
		assertEquals(1, cmds2.size());
		assertFalse((cmds2.get(0) instanceof ExceptionCommand));

	}
	
	@Test
	public void testCrappyDataBetweenCommands() throws IOException, StorageException, ClosedConnectionException{
		when(input.noDataAvailable()).thenReturn(false, false, false, true);
		when(input.readTextLine()).thenReturn("get testkey noreply\r\n     ", "     set testkey 888 0 20\r\n      ");
		when(input.getBytes(22)).thenReturn("aaaabbbbccccddddeeee\r\n".getBytes());
		
		when(sal.set((Item) anyObject())).thenAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {
				Item it = (Item)(invocation.getArguments()[0]);
				assertEquals("aaaabbbbccccddddeeee", new String(it.getData()));
				return null;
			}
			
		});
		
		List<ServerCommand> cmds = cmdreader.decodeCommands();
		assertEquals(2, cmds.size());
		assertFalse((cmds.get(0) instanceof ExceptionCommand));
		assertFalse((cmds.get(1) instanceof ExceptionCommand));
		cmds.get(1).attachCommandWriter(resp);
		cmds.get(1).execCommand();
		verify(input, times(1)).precacheDataFromRemote();
	}
}
