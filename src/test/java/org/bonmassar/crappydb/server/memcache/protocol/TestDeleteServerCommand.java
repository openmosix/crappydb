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

import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;


public class TestDeleteServerCommand extends TestCase {

	private DeleteServerCommand command;
	private StorageAccessLayer storage;
	private OutputCommandWriter output;
	
	@Before
	public void setUp() {
		command = new DeleteServerCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(OutputCommandWriter.class);
		command.setStorage(storage);
		command.channel = output;
	}

	@Test
	public void testShouldThrowExceptionWithTooFewParams() {
		try {
			command.parseCommandParams(" \r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowExceptionWithTooManyParams() {
		try {
			command.parseCommandParams("terminenzio 1235 noreply babuu\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldSupportNoReplyWithNoTime() throws ErrorException {
		command.parseCommandParams("terminenzio noreply\r\n");

		assertEquals(1, command.getNoReplyPosition());
	}
	
	@Test
	public void testShouldSupportNoReplyWithTime() throws ErrorException {
		command.parseCommandParams("terminenzio 12345 noreply\r\n");

		assertEquals(2, command.getNoReplyPosition());
	}
	
	@Test
	public void testRainbow() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzio noreply\r\n");

		command.execCommand();
		
		verify(storage, times(1)).delete(new Key("terminenzio"), -1L);
		verify(output, times(1)).writeToOutstanding("DELETED\r\n");
	}
	
	@Test
	public void testRainbowWithTime() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzio 1123456 noreply\r\n");

		command.execCommand();
		
		verify(storage, times(1)).delete(new Key("terminenzio"), 1123456L);
		verify(output, times(1)).writeToOutstanding("DELETED\r\n");
	}
	
	@Test
	public void testKeyNotFound() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzio noreply\r\n");

		CrappyDBException exception = new NotFoundException(); 
		doThrow(exception).when(storage).delete(new Key("terminenzio"), -1L);
		
		command.execCommand();
		
		verify(output, times(1)).writeException(exception);
	}
	
	@Test
	public void testKeyNotFoundWithTime() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzio 18882828 noreply\r\n");

		CrappyDBException exception = new NotFoundException(); 
		doThrow(exception).when(storage).delete(new Key("terminenzio"), 18882828L);
		
		command.execCommand();
		
		verify(output, times(1)).writeException(exception);
	}
	
	@Test
	public void testErrorOnStorage() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzio noreply\r\n");

		CrappyDBException exception = new StorageException("BOOM!");
		doThrow(exception).when(storage).delete(new Key("terminenzio"), -1L);
		
		command.execCommand();
		
		verify(output, times(1)).writeException(exception);
	}
	
	@Test
	public void testErrorOnStorageWithTime() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzio 888777333 noreply\r\n");

		CrappyDBException exception = new StorageException("BOOM!");
		doThrow(exception).when(storage).delete(new Key("terminenzio"), 888777333L);
		
		command.execCommand();
		
		verify(output, times(1)).writeException(exception);
	}
	
	@Test
	public void testVeryLongLongKey() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzioterminenzioterminenzioterminenzioterminenzioter" +
				"minenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenziotermi" +
				"nenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminen" +
				"zioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzio noreply\r\n");

		doThrow(new StorageException("BOOM!")).when(storage).delete(new Key("terminenzio"), -1L);
		
		command.execCommand();
		
		verify(storage, times(1)).delete(new Key("terminenzioterminenzioterminenzioterminenzioterminenzi" +
				"oterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzi" +
				"oterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzi" +
				"oterminenzioterminen"), -1L);
		verify(output, times(1)).writeToOutstanding("DELETED\r\n");		
	}
	
	@Test
	public void testVeryLongLongKeyWithTime() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("terminenzioterminenzioterminenzioterminenzioterminenzioter" +
				"minenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenziotermi" +
				"nenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminen" +
				"zioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzio 999888777 noreply\r\n");

		doThrow(new StorageException("BOOM!")).when(storage).delete(new Key("terminenzio"), 999888777L);
		
		command.execCommand();
		
		verify(storage, times(1)).delete(new Key("terminenzioterminenzioterminenzioterminenzioterminenzi" +
				"oterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzi" +
				"oterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzioterminenzi" +
				"oterminenzioterminen"), 999888777L);
		verify(output, times(1)).writeToOutstanding("DELETED\r\n");		
	}
	
	@Test
	public void testToStringNoReplyNoTime() throws ErrorException {
		command.parseCommandParams("terminenzio noreply\r\n");
		
		assertEquals("{Delete key=terminenzio time=-1 noreply=true}", command.toString());
	}
	
	@Test
	public void testToStringNoReplyTime() throws ErrorException {
		command.parseCommandParams("terminenzio 12345 noreply\r\n");
		
		assertEquals("{Delete key=terminenzio time=12345 noreply=true}", command.toString());
	}
	
	@Test
	public void testToStringOnlyTime() throws ErrorException {
		command.parseCommandParams("terminenzio 12345\r\n");
		assertEquals("{Delete key=terminenzio time=12345 noreply=false}", command.toString());
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("terminenzio\r\n");
		assertEquals("{Delete key=terminenzio time=-1 noreply=false}", command.toString());
	}

	
	
}
