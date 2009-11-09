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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.io.CommunicationTask.CommunicationDelegate;
import org.junit.Before;
import org.junit.Test;

public class TestCommunicationTask extends TestCase {

	private CommunicationTask frontend;
	private TransportProtocol tcp; 
	private TransportProtocol udp; 
	private CommunicationDelegate delegateTcp;
	private CommunicationDelegate delegateUdp;
	private SelectionKey selection;
	private SelectableChannel channel;
	
	@Before
	public void setUp() {
		tcp = mock(TransportProtocol.class);
		udp = mock(TransportProtocol.class);
		delegateTcp = mock(CommunicationDelegate.class);
		delegateUdp = mock(CommunicationDelegate.class);
		selection = mock(SelectionKey.class);
		frontend = new CommunicationTask(tcp, udp, selection);
		channel = mock(SelectableChannel.class);
	}
	
	@Test
	public void testNoOps() throws Exception {
		when(selection.readyOps()).thenReturn(0);
		
		frontend.call();
		verify(tcp, times(0)).comms();
		verify(udp, times(0)).comms();
	}
	
	@Test
	public void testOnlyReadNoCmdsTcp() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(selection.channel()).thenReturn(channel);
		when(tcp.comms()).thenReturn(delegateTcp);
		when(udp.isValidChannel(channel)).thenReturn(false);

		frontend.call();
		verify(udp, times(0)).comms();
		verify(delegateTcp, times(1)).read(selection);
	}
	
	@Test
	public void testOnlyReadNoCmdsUdp() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(selection.channel()).thenReturn(channel);
		when(udp.comms()).thenReturn(delegateUdp);
		when(udp.isValidChannel(channel)).thenReturn(true);

		frontend.call();
		verify(tcp, times(0)).comms();
		verify(delegateUdp, times(1)).read(selection);
	}
	
	@Test
	public void testOnlyReadIOErrorTcp() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(selection.channel()).thenReturn(channel);
		when(tcp.comms()).thenReturn(delegateTcp);
		when(udp.isValidChannel(channel)).thenReturn(false);
		when(delegateTcp.read(selection)).thenReturn(null);
				
		frontend.call();
		verify(delegateTcp, times(0)).accept(selection);		
		verify(delegateTcp, times(0)).write(selection);		
	}
	
	@Test
	public void testOnlyReadIOErrorUdp() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_READ);
		when(selection.channel()).thenReturn(channel);
		when(udp.comms()).thenReturn(delegateUdp);
		when(udp.isValidChannel(channel)).thenReturn(true);
		when(delegateUdp.read(selection)).thenReturn(null);
				
		frontend.call();
		verify(delegateUdp, times(0)).accept(selection);		
		verify(delegateUdp, times(0)).write(selection);		
	}
	
	@Test
	public void testOnlyWriteTcp() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_WRITE);
		when(selection.channel()).thenReturn(channel);
		when(tcp.comms()).thenReturn(delegateTcp);
		when(udp.isValidChannel(channel)).thenReturn(false);

		frontend.call();
		verify(delegateTcp, times(0)).accept(selection);		
		verify(delegateTcp, times(1)).write(selection);
		verify(delegateTcp, times(0)).read(selection);
	}

	@Test
	public void testOnlyWriteUdp() throws Exception {
		when(selection.readyOps()).thenReturn(SelectionKey.OP_WRITE);
		when(selection.channel()).thenReturn(channel);
		when(udp.comms()).thenReturn(delegateUdp);
		when(udp.isValidChannel(channel)).thenReturn(true);

		frontend.call();
		verify(delegateUdp, times(0)).accept(selection);		
		verify(delegateUdp, times(1)).write(selection);
		verify(delegateUdp, times(0)).read(selection);
	}

	@Test
	public void testOnlyAcceptTcp() throws Exception{
		when(selection.readyOps()).thenReturn(SelectionKey.OP_ACCEPT);
		when(selection.channel()).thenReturn(channel);
		when(tcp.comms()).thenReturn(delegateTcp);
		when(tcp.isValidChannel(channel)).thenReturn(true);

		frontend.call();
		verify(delegateTcp, times(1)).accept(selection);		
		verify(delegateTcp, times(0)).write(selection);
		verify(delegateTcp, times(0)).read(selection);
	}
	
	@Test
	//If that happen the accept on udp should blow
	public void testOnlyAcceptUdp() throws Exception{
		when(selection.readyOps()).thenReturn(SelectionKey.OP_ACCEPT);
		when(selection.channel()).thenReturn(channel);
		when(udp.comms()).thenReturn(delegateUdp);
		when(tcp.isValidChannel(channel)).thenReturn(false);

		frontend.call();
		verify(delegateUdp, times(1)).accept(selection);		
		verify(delegateUdp, times(0)).write(selection);
		verify(delegateUdp, times(0)).read(selection);
	}
}