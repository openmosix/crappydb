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

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;

import junit.framework.TestCase;

public class TestBackendPoolExecutor extends TestCase {

	private BackendPoolExecutor backend;
	
	@Before
	public void setUp() {
		backend = new BackendPoolExecutor();
	}
	
	@After
	public void tearDown() {
		Registry.INSTANCE.clear();
	}
	
	@Test
	public void testBeingExecuted() throws InterruptedException{
		List<ServerCommand> commands = getCommands();
		for (ServerCommand cmd : commands)
			backend.offer(cmd);
		
		Thread.sleep(5000);
		
		for (ServerCommand cmd : commands)
			verify(cmd, times(1)).execCommand();
		
		assertEquals(1, Registry.INSTANCE.size());
	}

	private List<ServerCommand> getCommands() {
		List<ServerCommand> cmds = new ArrayList<ServerCommand>();
		for(int i = 0; i < 1000; i++)
			cmds.add(mock(ServerCommand.class));
		
		return cmds;
	}
}
