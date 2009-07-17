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

import static org.mockito.Mockito.mock;

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestGetServerCommand extends TestCase {

	private GetServerCommand command;
	
	private StorageAccessLayer storage;
	private OutputCommandWriter output;
	
	@Before
	public void setUp() {
		command = new GetServerCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(OutputCommandWriter.class);
		command.setStorage(storage);
		command.channel = output;
	}
	
	@Test
	public void testShouldNotSupportNoReply() {
		assertEquals(-1, command.getNoReplyPosition());
	}
	
	@Test
	public void testToStringMultpleKeys() throws ErrorException {
		command.parseCommandParams("key1 key2 key3 key4");
		assertEquals("{Get key1=key1 key2=key2 key3=key3 key4=key4 }", command.toString());
	}
	
	@Test
	public void testToStringOneKey() throws ErrorException {
		command.parseCommandParams("key1");
		assertEquals("{Get key1=key1 }", command.toString());
	}
	
	@Test
	public void testShouldReturnExceptionWithNullKey() {
		try {
			command.parseCommandParams(null);
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldReturnExceptionWithNoKey() {
		try {
			command.parseCommandParams("");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldReturnExceptionWithEmptyKey() {
		try {
			command.parseCommandParams("       ");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
}
