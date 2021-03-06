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

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.junit.Before;
import org.junit.Test;

public class TestCommandFactory {
	
	private CommandFactoryDelegate factory;
	
	@Before
	public void setUp() throws ParseException{
		Configuration.INSTANCE.parse(null);
		factory = new CommandFactoryDelegate(null);
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
	public void testSetCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("set terminenzio 1 23 90\r\n");
		assertTrue(sc instanceof SetServerCommand);
	}

	@Test
	public void testGetCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("get");
		assertTrue(sc instanceof GetServerCommand);
	}
	
	@Test
	public void testGetCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("get terminenzio\r\n");
		assertTrue(sc instanceof GetServerCommand);
	}

	
	@Test
	public void testDeleteCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("delete");
		assertTrue(sc instanceof DeleteServerCommand);
	}
	
	@Test
	public void testDeleteCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("delete terminenzio\r\n");
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
		assertTrue(sc instanceof GetsServerCommand);
	}
	
	@Test
	public void testGetsCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("gets terminenzio\r\n");
		assertTrue(sc instanceof GetsServerCommand);
	}

	@Test
	public void testVersionCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("version");
		assertTrue(sc instanceof VersionServerCommand);
	}
	
	@Test
	public void testVersionCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("version\r\n");
		assertTrue(sc instanceof VersionServerCommand);
	}
	
	@Test
	public void testVerbosityCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("verbosity 4 noreply\r\n");
		assertTrue(sc instanceof VerbosityServerCommand);
	}
	
	@Test
	public void testVerbosityCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("verbosity");
		assertTrue(sc instanceof VerbosityServerCommand);
	}
	
	@Test
	public void testAddCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("add terminenzio 4 10 22 noreply\r\n");
		assertTrue(sc instanceof AddServerCommand);
	}
	
	@Test
	public void testAddCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("add");
		assertTrue(sc instanceof AddServerCommand);
	}
	
	@Test
	public void testReplaceCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("replace terminenzio 4 10 22 noreply\r\n");
		assertTrue(sc instanceof ReplaceServerCommand);
	}
	
	@Test
	public void testReplaceCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("replace");
		assertTrue(sc instanceof ReplaceServerCommand);
	}
	
	@Test
	public void testAppendCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("append terminenzio 4 10 22 noreply\r\n");
		assertTrue(sc instanceof AppendServerCommand);
	}
	
	@Test
	public void testAppendCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("append");
		assertTrue(sc instanceof AppendServerCommand);
	}
	
	@Test
	public void testPrependCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("prepend terminenzio 4 10 22 noreply\r\n");
		assertTrue(sc instanceof PrependServerCommand);
	}
	
	@Test
	public void testPrependCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("prepend");
		assertTrue(sc instanceof PrependServerCommand);
	}
	
	@Test
	public void testIncrementCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("incr terminenzio 4 noreply\r\n");
		assertTrue(sc instanceof IncrServerCommand);
	}
	
	@Test
	public void testIncrementCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("incr");
		assertTrue(sc instanceof IncrServerCommand);
	}
	
	@Test
	public void testDecrementCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("decr terminenzio 4 noreply\r\n");
		assertTrue(sc instanceof DecrServerCommand);
	}
	
	@Test
	public void testDecrementCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("decr");
		assertTrue(sc instanceof DecrServerCommand);
	}
	
	@Test
	public void testCasCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("cas terminenzio 4 22 8 88888 noreply\r\n");
		assertTrue(sc instanceof CasServerCommand);
	}
	
	@Test
	public void testCasCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("cas");
		assertTrue(sc instanceof CasServerCommand);
	}
	
	@Test
	public void testFlushCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("flush_all\r\n");
		assertTrue(sc instanceof FlushServerCommand);
	}
	
	@Test
	public void testFlushCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("flush_all");
		assertTrue(sc instanceof FlushServerCommand);
	}
	
	@Test
	public void testStatsCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("stats\r\n");
		assertTrue(sc instanceof StatsCommand);
	}
	
	@Test
	public void testStatsCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("stats");
		assertTrue(sc instanceof StatsCommand);
	}
	
	@Test
	public void testQuitCommandFromCommandLine() throws ErrorException {
		ServerCommand sc = factory.getCommandFromCommandLine("quit\r\n");
		assertTrue(sc instanceof QuitServerCommand);
	}
	
	@Test
	public void testQuitCommand() throws ErrorException {
		ServerCommand sc = factory.getCommand("quit");
		assertTrue(sc instanceof QuitServerCommand);
	}
}
