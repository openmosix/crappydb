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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.CommandResponse;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestFlushServerCommand extends TestCase {

	private FlushServerCommand command;
	private StorageAccessLayer storage;
	private CommandResponse output;
	
	@Before
	public void setUp() {
		command = new FlushServerCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(CommandResponse.class);
		command.setStorage(storage);
		command.channel = output;
	}
	
	@Test
	public void testShouldThrowExceptionWithTooManyParams() {
		try {
			command.parseCommandParams("90 noreply babuu\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldSupportNoReplyWithTime() throws ErrorException {
		command.parseCommandParams("90 noreply\r\n");
		assertEquals(1, command.getNoReplyPosition());
	}
	
	@Test
	public void testShouldSupportNoReplyWithoutTime() throws ErrorException {
		command.parseCommandParams("noreply\r\n");
		assertEquals(0, command.getNoReplyPosition());
	}
	
	@Test
	public void testRainbowFullParams() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("12345 noreply\r\n");

		command.execCommand();
		
		verify(storage, times(1)).flush(Long.valueOf(12345));
		verify(output, times(1)).writeToOutstanding("OK\r\n");
	}

	@Test
	public void testRainbowNoTime() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("noreply\r\n");

		command.execCommand();
		
		verify(storage, times(1)).flush(Long.valueOf(-1));
		verify(output, times(1)).writeToOutstanding("OK\r\n");
	}
	
	@Test
	public void testStorageException() throws ErrorException, StorageException {
		CrappyDBException exc = new StorageException("BOOM!");
		doThrow(exc).when(storage).flush(Long.valueOf(-1L));
		command.parseCommandParams("noreply\r\n");
		command.execCommand();
		
		verify(storage, times(1)).flush(Long.valueOf(-1L));
		verify(output, times(1)).writeException(exc);		
	}
	
	@Test
	public void testRainbow() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("noreply\r\n");

		command.execCommand();
		
		verify(storage, times(1)).flush(Long.valueOf(-1));
		verify(output, times(1)).writeToOutstanding("OK\r\n");
	}
	
	@Test
	public void testToStringNoReplyNoTime() throws ErrorException {
		command.parseCommandParams("noreply\r\n");
		
		assertEquals("{Flush time=-1 noreply=true}", command.toString());
	}
	
	@Test
	public void testToStringNoReplyTime() throws ErrorException {
		command.parseCommandParams("12345 noreply\r\n");
		
		assertEquals("{Flush time=12345 noreply=true}", command.toString());
	}
	
	@Test
	public void testToStringOnlyTime() throws ErrorException {
		command.parseCommandParams("12345\r\n");
		assertEquals("{Flush time=12345 noreply=false}", command.toString());
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("");
		assertEquals("{Flush time=-1 noreply=false}", command.toString());
	}
	
}
