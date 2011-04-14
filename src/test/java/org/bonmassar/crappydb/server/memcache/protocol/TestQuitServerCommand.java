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

import org.bonmassar.crappydb.server.exceptions.ClosedConnectionException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.CommandResponse;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestQuitServerCommand extends TestCase {
	
	private QuitServerCommand command;
	private StorageAccessLayer storage;
	private CommandResponse output;
	
	@Before
	public void setUp() {
		command = new QuitServerCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(CommandResponse.class);
		command.setStorage(storage);
		command.channel = output;
	}
	
	@Test
	public void testShouldThrowExceptionWithTooManyParams() {
		try {
			command.parseCommandParams("90\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldNotSupportNoReply() throws ErrorException {
		assertEquals(-1, command.getNoReplyPosition());
	}
		
	@Test
	public void testRainbow() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("");
		try{
			command.execCommand();
		}catch (ClosedConnectionException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("");
		
		assertEquals("{Quit}", command.toString());
	}	
}
