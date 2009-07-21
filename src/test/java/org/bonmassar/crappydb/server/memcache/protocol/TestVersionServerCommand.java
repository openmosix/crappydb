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

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class TestVersionServerCommand extends TestCase {

	private VersionServerCommand command;
	private OutputCommandWriter writer;
	
	@Before
	public void setUp(){
		command = new VersionServerCommand();
		writer = mock(OutputCommandWriter.class);
		command.attachCommandWriter(writer);
	}
	
	@Test
	public void testSupportNoReply() {
		assertEquals(-1, command.getNoReplyPosition());
	}
	
	@Test
	public void testSupportZeroParams() {
		try {
			command.parseCommandParams("");
		} catch (ErrorException e) {
			fail();
		}
	}
	
	@Test
	public void testSupportNullParams() {
		try {
			command.parseCommandParams(null);
		} catch (ErrorException e) {
			fail();
		}
	}
	
	@Test
	public void testSupportMultiParams() {
		try {
			command.parseCommandParams("123 456");
		} catch (ErrorException e) {
			fail();
		}
	}
	
	@Test
	public void testShouldReturnVersionNumber() {
		command.execCommand();
		verify(writer, times(1)).writeToOutstanding("VERSION 0.1\r\n");
	}
	
}
