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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.exceptions.ClosedConnectionException;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactoryDelegate;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.Before;
import org.junit.Test;

public class TestFrontendTask extends TestCase {

	private CommunicationTask frontend;
	private CommandFactoryDelegate cmdFactory;
	private TransportProtocol transport; 
	private SelectionKey selection;
	private TransportSession esConnection;
	
	class FakeFrontendTask extends CommunicationTask{

		public FakeFrontendTask(CommandFactoryDelegate cmdFactory,
				TransportProtocol protocol,
				SelectionKey key) {
			super(cmdFactory, protocol, key);
		}
	}
	
	@Before
	public void setUp() {
		cmdFactory = mock(CommandFactoryDelegate.class);
		transport = mock(TransportProtocol.class);
		selection = mock(SelectionKey.class);
		frontend = new FakeFrontendTask(cmdFactory, transport, selection);
		esConnection = mock(TransportSession.class);
		when(transport.getChannel((CommandFactoryDelegate) anyObject(), (SelectionKey) anyObject())).thenReturn(esConnection);
	}
	
	@Test
	public void testNoOps() throws Exception {
		when(selection.readyOps()).thenReturn(0);
		
		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());
		verify(esConnection, times(0)).doWrite();
	}
	
	@Test
	public void testOnlyReadNoCmds() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(esConnection.doRead()).thenReturn(new ArrayList<ServerCommand>());
		
		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(esConnection, times(0)).doWrite();
	}
	
	@Test
	public void testOnlyReadIOError() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(esConnection.doRead()).thenReturn(null);
		
		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(esConnection, times(0)).doWrite();
	}
	
	@Test
	public void testOnlyReadMultipleCmds() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		ServerCommand cmd3 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2, cmd3));
		
		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(cmd1, times(1)).execCommand();
		verify(cmd2, times(1)).execCommand();
		verify(cmd3, times(1)).execCommand();
		verify(esConnection, times(0)).doWrite();
	}
	
	@Test
	public void testOnlyReadMultipleCmdsAndQuit() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		ServerCommand cmd3 = mock(ServerCommand.class);
		
		doThrow(new ClosedConnectionException()).when(cmd2).execCommand();
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2, cmd3));
		
		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(cmd1, times(1)).execCommand();
		verify(cmd2, times(1)).execCommand();
		verify(cmd3, times(0)).execCommand();
		verify(esConnection, times(0)).doWrite();
		verify(esConnection, times(1)).doClose();
	}
	
	@Test
	public void testOnlyReadOneCmd() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1));
		
		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(cmd1, times(1)).execCommand();
		verify(esConnection, times(0)).doWrite();
	}
	
	@Test
	public void testOnlyWrite() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_WRITE);

		frontend.call();
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(esConnection, times(1)).doWrite();
	}
		
	@Test
	public void testOnlyAccept() throws Exception{
		when(selection.readyOps()).thenReturn(SelectionKey.OP_ACCEPT);
		
		frontend.call();
		verify(transport, times(1)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(esConnection, times(0)).doWrite();
	}
	
	@Test
	public void testAcceptReadWrite() throws Exception{
		when(selection.readyOps()).thenReturn(SelectionKey.OP_ACCEPT | SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2));
		
		frontend.call();
		
		verify(transport, times(1)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(cmd1, times(1)).execCommand();
		verify(cmd2, times(1)).execCommand();
		verify(esConnection, times(1)).doWrite();
	}
	
	@Test
	public void testReadWrite() throws Exception{
		when(selection.readyOps()).thenReturn(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		ServerCommand cmd1 = mock(ServerCommand.class);
		ServerCommand cmd2 = mock(ServerCommand.class);
		when(esConnection.doRead()).thenReturn(Arrays.asList(cmd1, cmd2));
		
		frontend.call();
		
		verify(transport, times(0)).accept((CommandFactoryDelegate)anyObject(), (SelectionKey) anyObject());		
		verify(cmd1, times(1)).execCommand();
		verify(cmd2, times(1)).execCommand();
		verify(esConnection, times(1)).doWrite();
	}
}
