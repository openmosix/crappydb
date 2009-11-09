package org.bonmassar.crappydb.server.io;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;
import java.util.Arrays;

import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.Test;

public class TestCommunicationDelegate {
	/*@Test
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
	
	*/
	

	
	/*
	
		
	
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
	}*/
}
