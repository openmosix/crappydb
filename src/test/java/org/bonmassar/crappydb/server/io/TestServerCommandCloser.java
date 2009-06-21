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
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class TestServerCommandCloser {

	class MockCommandCloserChannel extends ServerCommandCloser {

		private boolean statusChannel;
		private boolean closedCalled;
		private boolean blowWithException;

		public MockCommandCloserChannel(SelectionKey key) {
			super(key);
			statusChannel = true;
			closedCalled = false;
			blowWithException = false;
		}

		@Override
		protected boolean isChannelOpen(SocketChannel sc) {
			return null != sc && statusChannel;
		}

		public void closeTheChannel() {
			statusChannel = false;
		}
		
		public void stubIOErrors() {
			blowWithException = true;
		}

		@Override
		protected void closeDescriptor(SocketChannel sc) throws IOException {
			closedCalled = true;
			if(blowWithException)
				throw new IOException("BOOM!");
		}
		
		public boolean isClosedCalled(){
			return closedCalled;
		}
	}

	private ServerCommandCloser closer;
	private SelectionKey selector;
	private SocketChannel channel;

	@Before
	public void setUp() {
		selector = mock(SelectionKey.class);
		channel = mock(SocketChannel.class);
		closer = new MockCommandCloserChannel(selector);
	}

	@Test
	public void testCloseConnectionAlreadyClosed() {
		closer.state = ServerCommandCloser.DBConnectionStatus.CLOSED;

		closer.closeConnection();

		verify(selector, times(0)).channel();
	}

	@Test
	public void testCloseConnectionButNoChannel() {
		((MockCommandCloserChannel) closer).closeTheChannel();
		when(selector.channel()).thenReturn(null);

		closer.closeConnection();
		verify(selector, times(1)).channel();
	}

	@Test
	public void testCloseConnectionRainbowScenario() throws IOException {
		Selector intselector = mock(Selector.class);
		when(selector.selector()).thenReturn(intselector);
		when(selector.channel()).thenReturn(channel);

		closer.closeConnection();
		assertTrue(((MockCommandCloserChannel)closer).isClosedCalled());
	}

	@Test
	public void testCloseConnectionIOProblems() {
		((MockCommandCloserChannel)closer).stubIOErrors();
		Selector intselector = mock(Selector.class);
		when(selector.selector()).thenReturn(intselector);
		when(selector.channel()).thenReturn(channel);

		closer.closeConnection();
		assertTrue(((MockCommandCloserChannel)closer).isClosedCalled());
	}

}
