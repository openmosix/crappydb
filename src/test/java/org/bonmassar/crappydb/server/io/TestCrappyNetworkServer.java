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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCrappyNetworkServer {

	private CrappyNetworkServer server;
	private CommandFactory cmdFactory;
	
	@Before
	public void setUp() throws ParseException {
		cmdFactory = mock(CommandFactory.class);
		Configuration.INSTANCE.parse(null);
	}
	
	@After
	public void tearDown() {
		Registry.INSTANCE.clear();
		server = null;
	}
	
	@Test
	public void testSetupIOError() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--port", "-42"});
		try{
			server = new CrappyNetworkServer(cmdFactory);
		}catch(RuntimeException re){
			return;
		}
		fail();
	}
	
	@Test
	public void testSetupOk() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--port", "11211"});
		server = new CrappyNetworkServer(cmdFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartServer() throws IOException, ParseException, InterruptedException {
		Configuration.INSTANCE.parse(new String[]{"--port", "11213"});
		server = new CrappyNetworkServer(cmdFactory);
		server.serverSelector = mock(Selector.class);
		server.workers = mock(DBPoolThreadExecutor.class);
		Set<SelectionKey> keys = mock(Set.class);
		Iterator<SelectionKey> it = mock(Iterator.class);
		SelectionKey selKey1 = mock(SelectionKey.class);
		SelectionKey selKey2 = mock(SelectionKey.class);
		Future<Void> task1= mock(Future.class);
		Future<Void> task2= mock(Future.class);
		
		when(server.serverSelector.select()).thenReturn(8989);
		when(server.workers.submit(selKey1)).thenReturn(task1);
		when(server.workers.submit(selKey2)).thenReturn(task2);

		when(keys.iterator()).thenReturn(it);
		when(it.hasNext()).thenReturn(true, true, false);
		when(it.next()).thenReturn(selKey1, selKey2);
		
		when(server.serverSelector.selectedKeys()).thenReturn(keys);
		
		server.processRequests();
		verify(server.workers, times(1)).submit(selKey1);
		verify(server.workers, times(1)).submit(selKey2);
	}
	
	@Test
	public void testStartServerIOException() throws IOException, ParseException, InterruptedException {
		Configuration.INSTANCE.parse(new String[]{"--port", "11215"});

		server = new CrappyNetworkServer(cmdFactory);
		server.serverSelector = mock(Selector.class);
		server.workers = mock(DBPoolThreadExecutor.class);

		when(server.serverSelector.select()).thenThrow(new IOException("BOOM!"));
		
		server.processRequests();
		verify(server.workers, times(0)).submit((SelectionKey) anyObject());
	}
	
}
