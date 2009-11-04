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

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.anyString;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactoryDelegate;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestServerCommandFragment extends TestCase {

	private ServerCommand whatever;
	private ServerCommandFragment fragment;
	private CommandFactoryDelegate commandFactory;
	
	@Before
	public void setUp() {
		commandFactory = mock(CommandFactoryDelegate.class);
		fragment = new ServerCommandFragment(commandFactory);
		whatever = mock(ServerCommand.class);
	}
	
	@Test
	public void testInitStatus() {
		assertStatusZero();
		verify(commandFactory, times(0)).getCommandFromCommandLine(anyString());
	}
	
	@Test
	public void testReset() {
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(whatever);
		assertTrue(fragment.addCommandLineFragment("gaat testkey noreply\r\n"));
		assertEquals(whatever, fragment.getCommand());

		fragment.reset();
		assertStatusZero();
	}
	
	@Test
	public void testAddAllDataInOneShot() {
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(whatever);
		boolean result = fragment.addCommandLineFragment("gaat testkey noreply\r\n");
		assertTrue(result);
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(0, fragment.getContentLength());
		assertTrue(fragment.payloadReadCompleted());
		assertEquals(whatever, fragment.getCommand());
	}
	
	@Test
	public void testAddProgressivelyData(){
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(whatever);
		when(whatever.payloadContentLength()).thenReturn(17);
		assertFalse(fragment.addCommandLineFragment("gaat tes"));
		assertFalse(fragment.isCommandLineCompleted());
		assertFalse(fragment.addCommandLineFragment("tkey nor"));
		assertFalse(fragment.isCommandLineCompleted());
		assertFalse(fragment.addCommandLineFragment("eply\r"));
		assertFalse(fragment.isCommandLineCompleted());
		assertTrue(fragment.addCommandLineFragment("\n"));
		
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(17, fragment.getContentLength());
		assertFalse(fragment.payloadReadCompleted());
		assertEquals(whatever, fragment.getCommand());
	}
	
	@Test
	public void testAddProgressivelyDataWithCrappyEnd(){
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(whatever);
		when(whatever.payloadContentLength()).thenReturn(17);
		assertFalse(fragment.addCommandLineFragment("gaat tes"));
		assertFalse(fragment.addCommandLineFragment("tkey nor"));
		assertFalse(fragment.addCommandLineFragment("eply\r"));
		assertTrue(fragment.addCommandLineFragment("\n      "));
		
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(17, fragment.getContentLength());
		assertFalse(fragment.payloadReadCompleted());
		assertEquals(whatever, fragment.getCommand());
	}
	
	@Test
	public void testAddProgressivelyDataAndPayload(){
		when(commandFactory.getCommandFromCommandLine("gaat testkey noreply")).thenReturn(whatever);
		when(whatever.payloadContentLength()).thenReturn(17);
		assertFalse(fragment.addCommandLineFragment("gaat testkey nor"));
		assertTrue(fragment.addCommandLineFragment("eply\r\n"));
		
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(17, fragment.getContentLength());
		assertFalse(fragment.payloadReadCompleted());
		assertEquals(whatever, fragment.getCommand());
		
		fragment.addPayloadContentPart("0123456789".getBytes());
		assertFalse(fragment.payloadReadCompleted());
		fragment.addPayloadContentPart("1234567".getBytes());
		assertTrue(fragment.payloadReadCompleted());
		
		verify(whatever, times(1)).addPayloadContentPart("0123456789".getBytes());
		verify(whatever, times(1)).addPayloadContentPart("1234567".getBytes());
	}
		
	private void assertStatusZero() {
		assertFalse(fragment.commandAlreadyDecoded());
		assertFalse(fragment.isCommandLineCompleted());
		assertEquals(0, fragment.getContentLength());
		assertTrue(fragment.payloadReadCompleted());
		assertNull(fragment.getCommand());
	}
	
}
