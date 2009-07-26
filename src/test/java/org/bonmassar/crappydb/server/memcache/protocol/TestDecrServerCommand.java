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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestDecrServerCommand extends TestCase {
	
	private DecrServerCommand command;
	private StorageAccessLayer storage;
	private OutputCommandWriter output;
	
	@Before
	public void setUp() {
		command = new DecrServerCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(OutputCommandWriter.class);
		command.setStorage(storage);
		command.channel = output;
	}
	
	@Test
	public void testShouldThrowExceptionWithTooFewParams() {
		try {
			command.parseCommandParams("terminenzio\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowExceptionWithTooManyParams() {
		try {
			command.parseCommandParams("terminenzio 12 noreply babuu\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldSupportNoReply() {
		assertEquals(2, command.getNoReplyPosition());
	}
	
	@Test
	public void testShouldNotSupportContentLength() throws ErrorException {
		command.parseCommandParams("terminenzio 12 noreply\r\n");
		assertEquals(0, command.payloadContentLength());
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("terminenzio 12\r\n");
		assertEquals("{Decr key=terminenzio value=12 noreply=false}", command.toString());
	}
	
	@Test
	public void testToStringNoReply() throws ErrorException {
		command.parseCommandParams("terminenzio 12 noreply\r\n");
		assertEquals("{Decr key=terminenzio value=12 noreply=true}", command.toString());
	}
	
	@Test
	public void testRainbow() throws ErrorException, NotFoundException, StorageException {
		Item it = new Item(new Key("terminenzio"), "98".getBytes());
		doReturn(it).when(storage).decrease(new Key("terminenzio"), "12");
		
		command.parseCommandParams("terminenzio 12\r\n");
		command.execCommand();

		verify(command.channel, times(1)).writeToOutstanding("98".getBytes());
		verify(command.channel, times(1)).writeToOutstanding("\r\n");
	}
	
	@Test
	public void testError() throws ErrorException, NotFoundException, StorageException {
		CrappyDBException exception = new NotFoundException();
		doThrow(exception).when(storage).decrease(new Key("terminenzio"), "12");
		
		command.parseCommandParams("terminenzio 12\r\n");
		command.execCommand();

		verify(command.channel, times(1)).writeException(exception);
	}
}
