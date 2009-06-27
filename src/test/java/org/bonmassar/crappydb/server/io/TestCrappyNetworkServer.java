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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestCrappyNetworkServer extends TestCase {

	private CrappyNetworkServer server;
	private CommandFactory cmdFactory;
	
	@Before
	public void setUp() {
		cmdFactory = mock(CommandFactory.class);
	}
	
	@Test
	public void testSetupIOError() {
		server = new CrappyNetworkServer(cmdFactory, -42);
		try{
			server.serverSetup();
		}catch(RuntimeException re){
			return;
		}
		fail();
	}
	
	@Test
	public void testSetupOk() {
		server = new CrappyNetworkServer(cmdFactory, 11211);
		server.serverSetup();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartServer() throws IOException {
		server = new CrappyNetworkServer(cmdFactory, 11211);
		server.serverSelector = mock(Selector.class);
		server.frontend = mock(FrontendPoolExecutor.class);
		Set<SelectionKey> keys = mock(Set.class);
		Iterator<SelectionKey> it = mock(Iterator.class);
		SelectionKey selKey1 = mock(SelectionKey.class);
		SelectionKey selKey2 = mock(SelectionKey.class);
		
		when(server.serverSelector.select()).thenReturn(8989);
		when(keys.iterator()).thenReturn(it);
		when(it.hasNext()).thenReturn(true, true, false);
		when(it.next()).thenReturn(selKey1, selKey2);
		
		when(server.serverSelector.selectedKeys()).thenReturn(keys);
		
		server.processRequests();
		verify(server.frontend, times(1)).offer(selKey1);
		verify(server.frontend, times(1)).offer(selKey2);
	}
	
}
