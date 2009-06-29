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

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Arrays;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyObject;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestFrontendTask extends TestCase {

	private DynamicCyclicBarrier barrier;
	private FrontendTask frontend;
	private CommandFactory cmdFactory;
	private Selector parentSelector; 
	private BackendPoolExecutor backend;
	private ServerCommandAccepter accepter;
	private SelectionKey selection;
	private EstablishedConnection esConnection;
	private FrontendPoolExecutor frontendpool;
	
	class FakeFrontendTask extends FrontendTask{

		public FakeFrontendTask(CommandFactory cmdFactory,
				Selector parentSelector, BackendPoolExecutor backend,
				FrontendPoolExecutor fe) {
			super(cmdFactory, parentSelector, backend, fe);
		}
		
		@Override
		protected EstablishedConnection getChannel(SelectionKey sk) {
			return esConnection;
		}
		
	}
	
	@Before
	public void setUp() {
		barrier = mock(DynamicCyclicBarrier.class);
		cmdFactory = mock(CommandFactory.class);
		parentSelector = mock(Selector.class);
		backend = mock(BackendPoolExecutor.class);
		accepter = mock(ServerCommandAccepter.class);
		frontendpool = mock(FrontendPoolExecutor.class);
		frontend = new FakeFrontendTask(cmdFactory, parentSelector, backend, frontendpool);
		frontend.accepter = accepter;
		selection = mock(SelectionKey.class);
		esConnection = mock(EstablishedConnection.class);
	}
	
	@Test
	public void testNoOps() throws Exception {
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);
		when(selection.readyOps()).thenReturn(0);
		
		frontend.executeTask();
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());
		verify(backend, times(0)).offer((ServerCommand) anyObject());
		verify(esConnection, times(0)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testOnlyReadNoCmds() throws Exception {
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(esConnection.doRead()).thenReturn(new ArrayList<ServerCommand>());
		
		frontend.executeTask();
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(0)).offer((ServerCommand) anyObject());
		verify(esConnection, times(0)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testOnlyReadIOError() throws Exception {
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(esConnection.doRead()).thenReturn(null);
		
		frontend.executeTask();
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(0)).offer((ServerCommand) anyObject());
		verify(esConnection, times(0)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testOnlyReadMultipleCmds() throws InterruptedException {
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		ServerCommand cmd3 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2, cmd3));
		
		frontend.executeTask();
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(1)).offer(cmd1);
		verify(backend, times(1)).offer(cmd2);
		verify(backend, times(1)).offer(cmd3);
		verify(esConnection, times(0)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testOnlyReadOneCmd() throws InterruptedException {
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1));
		
		frontend.executeTask();
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(1)).offer(cmd1);
		verify(esConnection, times(0)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testOnlyWrite() throws InterruptedException {
		when(frontendpool.take()).thenReturn(selection);
		when(selection.readyOps()).thenReturn(SelectionKey.OP_WRITE);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		frontend.executeTask();
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(0)).offer((ServerCommand) anyObject());
		verify(esConnection, times(1)).doWrite();
		verify(barrier, times(1)).countDown();
	}
		
	@Test
	public void testOnlyAccept() throws InterruptedException{
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_ACCEPT);
		
		frontend.executeTask();
		verify(accepter, times(1)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(0)).offer((ServerCommand) anyObject());
		verify(esConnection, times(0)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testAcceptReadWrite() throws InterruptedException{
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_ACCEPT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2));
		
		frontend.executeTask();
		
		verify(accepter, times(1)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(1)).offer(cmd1);
		verify(backend, times(1)).offer(cmd2);
		verify(esConnection, times(1)).doWrite();
		verify(barrier, times(1)).countDown();
	}
	
	@Test
	public void testReadWrite() throws InterruptedException{
		when(frontendpool.take()).thenReturn(selection);
		when(frontendpool.getBarrier()).thenReturn(barrier);

		when(selection.readyOps()).thenReturn(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2));
		
		frontend.executeTask();
		
		verify(accepter, times(0)).doAccept((SelectionKey) anyObject());		
		verify(backend, times(1)).offer(cmd1);
		verify(backend, times(1)).offer(cmd2);
		verify(esConnection, times(1)).doWrite();
		verify(barrier, times(1)).countDown();
	}
}
