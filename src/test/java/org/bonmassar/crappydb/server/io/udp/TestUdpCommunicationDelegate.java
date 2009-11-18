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

package org.bonmassar.crappydb.server.io.udp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

import org.bonmassar.crappydb.server.exceptions.ClosedConnectionException;
import org.bonmassar.crappydb.server.io.TransportSession;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestUdpCommunicationDelegate {

	private UdpCommunicationDelegate delegate;
	
	@Before
	public void setUp() {
		delegate = new UdpCommunicationDelegate();
	}
	
	@Test
	public void testAccept() {
		try{
			delegate.accept(null);
		}catch(IllegalStateException ie){
			return;
		}
		fail();
	}
	
	@Test
	public void testWrite() {
		try{
			delegate.write(null);
		}catch(IllegalStateException ie){
			return;
		}
		fail();
	}
	
	@Test
	public void testGetSession() {
		SelectionKey key = Mockito.mock(SelectionKey.class);
		assertNotNull(delegate.getSession(key));
	}
	
	@Test
	public void testRead() throws ClosedConnectionException {
		final TransportSession session = Mockito.mock(TransportSession.class);
		delegate = new UdpCommunicationDelegate() {
			@Override
			public TransportSession getSession(SelectionKey sk) {
				return session;
			}
		};
		
		ServerCommand cmd1 = Mockito.mock(ServerCommand.class), cmd2 = Mockito.mock(ServerCommand.class);
		
		Mockito.when(session.doRead()).thenReturn(Arrays.asList(cmd1, cmd2));
		SelectionKey key = Mockito.mock(SelectionKey.class);
		assertNotNull(delegate.read(key));
		Mockito.verify(session, Mockito.times(1)).doWrite();
		Mockito.verify(cmd1, Mockito.times(1)).execCommand();
		Mockito.verify(cmd2, Mockito.times(1)).execCommand();
	}
}
