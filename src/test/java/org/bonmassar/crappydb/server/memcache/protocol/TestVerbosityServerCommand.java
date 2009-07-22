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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyObject;

public class TestVerbosityServerCommand extends TestCase {

	private VerbosityServerCommand command;
	private OutputCommandWriter writer;
	private Logger logger;
	
	@Before
	public void setUp() {
		writer = mock(OutputCommandWriter.class);
		logger = mock(Logger.class);
		command = new VerbosityServerCommand();
		command.channel = writer;
		command.logger = logger;
	}
	
	@Test
	public void testExceptionsOnNoParams() {
		try {
			command.parseCommandParams("");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testExceptionsOnNull() {
		try {
			command.parseCommandParams(null);
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testExceptionsOnTooManyParams() {
		try {
			command.parseCommandParams("3 noreply terminenzio");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testExceptionsOnNotIntegerParam() throws ErrorException {
		command.parseCommandParams("terminenzio noreply");
		command.execCommand();
		
		verify(writer, times(1)).writeException((ErrorException)anyObject());
	}
	
	@Test
	public void testRainbowScenario() throws ErrorException {
		command.parseCommandParams("3 noreply");
		command.execCommand();
		
		verify(writer, times(1)).writeToOutstanding("OK\r\n");
		verify(logger, times(1)).setLevel(Level.WARN);
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("1 noreply");
		assertEquals("{Verbosity log=FATAL}", command.toString());
	}
	
	@Test
	public void testNoReplyPosition() {
		assertEquals(1, command.getNoReplyPosition());
	}
}
