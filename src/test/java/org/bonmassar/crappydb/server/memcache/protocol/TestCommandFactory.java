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

package org.bonmassar.crappydb.server.memcache.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.junit.Before;
import org.junit.Test;

public class TestCommandFactory {
	
	private CommandFactory factory;
	
	@Before
	public void setUp(){
		factory = new CommandFactory(null);
	}
	
	@Test
	public void testNullCommand() {
		try {
			factory.getCommand(null);
		} catch (ErrorException e) {
			assertEquals("ErrorException [Invalid command]", e.toString());
		}
	}
	
	@Test
	public void testEmptyCommand() {
		try {
			factory.getCommand("");
		} catch (ErrorException e) {
			assertEquals("ErrorException [Invalid command]", e.toString());
		}
	}
	
	@Test
	public void testSetCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("set");
		assertTrue(sc instanceof SetServerCommand);
	}
	
	@Test
	public void testGetCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("get");
		assertTrue(sc instanceof GetServerCommand);
	}
	
	@Test
	public void testDeleteCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("delete");
		assertTrue(sc instanceof DeleteServerCommand);
	}
	
	@Test
	public void testNotExistingCommand() {
		try {
			factory.getCommand("terminenzio");
		} catch (ErrorException e) {
			assertEquals("ErrorException [Command not found]", e.toString());
		}
	}
	
	@Test
	public void testGetsCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("gets");
		assertTrue(sc instanceof GetServerCommand);
	}}
