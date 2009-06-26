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
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.bonmassar.crappydb.mocks.FakeSelectionKey;
import org.bonmassar.crappydb.mocks.WhateverChannel;
import org.bonmassar.crappydb.mocks.WhateverServerChannel;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

public class TestServerCommandAccepter extends TestCase {

	private CommandFactory cmdFactory;
	private Selector parentSelector;
	private ServerCommandAccepter accepter;
	private SelectionKey selection;
	private SelectionKey newSelKey;
	
	class FakeServerCommandAccepter extends ServerCommandAccepter{

		private SelectionKey newSelKey;
		private boolean blow;
		
		public FakeServerCommandAccepter(CommandFactory cmdFactory,
				Selector parentSelector, SelectionKey newSelKey) {
			super(cmdFactory, parentSelector);
			this.newSelKey = newSelKey;
		}
		
		public void pleaseBlow(){
			blow = true;
		}
		
		@Override
		protected void registerNewSocketToSelector(SocketChannel clientChannel,
				String connectionName) throws ClosedChannelException {
			if(blow)
				throw new ClosedChannelException();
			
			attachNewChannel(newSelKey, connectionName);
		}
	}
	
	@Before
	public void setUp() {
		cmdFactory = mock(CommandFactory.class);
		selection = mock(FakeSelectionKey.class);
		parentSelector = mock(Selector.class);
		newSelKey = mock(SelectionKey.class);
		accepter = new FakeServerCommandAccepter(cmdFactory, parentSelector, newSelKey);
	}
	
	@Test
	public void testAcceptNewClient() throws IOException{
		WhateverServerChannel schannel = mock(WhateverServerChannel.class);
		WhateverChannel channel = mock(WhateverChannel.class);
		Socket socket = mock(Socket.class);
		InetAddress address = mock(InetAddress.class);
		
		when(selection.channel()).thenReturn(schannel);
		when(schannel.accept()).thenReturn(channel);
		when(channel.socket()).thenReturn(socket);
		when(address.toString()).thenReturn("127.0.0.1", "127.0.0.1");
		when(socket.getInetAddress()).thenReturn(address);
		when(socket.getPort()).thenReturn(42, 42);
		
		accepter.doAccept(selection);
	}
	
	@Test
	public void testIOError() throws IOException {
		WhateverServerChannel schannel = mock(WhateverServerChannel.class);
		
		when(selection.channel()).thenReturn(schannel);
		when(schannel.accept()).thenThrow(new IOException("BOOM!"));

		accepter.doAccept(selection);
	}
	
	@Test
	public void testNullChannel(){
		when(selection.channel()).thenReturn(null);

		accepter.doAccept(selection);
	}
	
	@Test
	public void testClosedChannel() throws IOException{
		WhateverServerChannel schannel = mock(WhateverServerChannel.class);
		WhateverChannel channel = mock(WhateverChannel.class);
		Socket socket = mock(Socket.class);
		InetAddress address = mock(InetAddress.class);
		
		when(selection.channel()).thenReturn(schannel);
		when(schannel.accept()).thenReturn(channel);
		when(channel.socket()).thenReturn(socket);
		when(address.toString()).thenReturn("127.0.0.1", "127.0.0.1");
		when(socket.getInetAddress()).thenReturn(address);
		when(socket.getPort()).thenReturn(42, 42);
		
		((FakeServerCommandAccepter)accepter).pleaseBlow();
		accepter.doAccept(selection);
	}
	
	
	
}
