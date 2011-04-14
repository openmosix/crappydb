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

import junit.framework.TestCase;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.junit.Before;
import org.junit.Test;

public class TestServerCommandFragment extends TestCase {

	private ServerCommandFragment fragment;
	
	@Before
	public void setUp() throws ParseException {
		fragment = new ServerCommandFragment();
		CommandFactory.INSTANCE.setStorageLayer(null);
		Configuration.INSTANCE.parse(null);
	}
	
	@Test
	public void testInitStatus() {
		assertStatusZero();
	}
	
	@Test
	public void testReset() {
		assertTrue(fragment.addCommandLineFragment("get testkey noreply\r\n"));
		assertNotNull(fragment.getCommand());

		fragment.reset();
		assertStatusZero();
	}
	
	@Test
	public void testAddAllDataInOneShot() {
		boolean result = fragment.addCommandLineFragment("get testkey noreply\r\n");
		assertTrue(result);
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(0, fragment.getContentLength());
		assertTrue(fragment.payloadReadCompleted());
		assertNotNull(fragment.getCommand());
	}
	
	@Test
	public void testAddProgressivelyData(){
		assertFalse(fragment.addCommandLineFragment("set term"));
		assertFalse(fragment.isCommandLineCompleted());
		assertFalse(fragment.addCommandLineFragment("inenzio 12"));
		assertFalse(fragment.isCommandLineCompleted());
		assertFalse(fragment.addCommandLineFragment(" 500 4\r"));
		assertFalse(fragment.isCommandLineCompleted());
		assertTrue(fragment.addCommandLineFragment("\n"));
		
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(6, fragment.getContentLength());
		assertFalse(fragment.payloadReadCompleted());
		assertNotNull(fragment.getCommand());
	}
	
	@Test
	public void testAddProgressivelyDataWithCrappyEnd(){
		assertFalse(fragment.addCommandLineFragment("set term"));
		assertFalse(fragment.addCommandLineFragment("inenzio 12"));
		assertFalse(fragment.addCommandLineFragment(" 500 4\r"));
		assertTrue(fragment.addCommandLineFragment("\n      "));
		
		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(6, fragment.getContentLength());
		assertFalse(fragment.payloadReadCompleted());
		assertNotNull(fragment.getCommand());
	}
	
	@Test
	public void testAddProgressivelyDataAndPayload(){
		assertFalse(fragment.addCommandLineFragment("set term"));
		assertFalse(fragment.addCommandLineFragment("inenzio 12"));
		assertFalse(fragment.addCommandLineFragment(" 500 17\r"));
		assertTrue(fragment.addCommandLineFragment("\n"));

		assertTrue(fragment.commandAlreadyDecoded());
		assertTrue(fragment.isCommandLineCompleted());
		assertEquals(19, fragment.getContentLength());
		assertFalse(fragment.payloadReadCompleted());
		assertNotNull(fragment.getCommand());
		
		fragment.addPayloadContentPart("0123456789".getBytes());
		assertFalse(fragment.payloadReadCompleted());
		assertEquals(9, fragment.getContentLength());
		fragment.addPayloadContentPart("1234567\r\n".getBytes());
		assertTrue(fragment.payloadReadCompleted());		
	}
		
	private void assertStatusZero() {
		assertFalse(fragment.commandAlreadyDecoded());
		assertFalse(fragment.isCommandLineCompleted());
		assertEquals(0, fragment.getContentLength());
		assertTrue(fragment.payloadReadCompleted());
		assertNull(fragment.getCommand());
	}
	
}
