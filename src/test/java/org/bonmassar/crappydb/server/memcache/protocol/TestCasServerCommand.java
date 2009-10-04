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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import junit.framework.TestCase;

public class TestCasServerCommand extends TestCase {
	
	private CasServerCommand command;
	private StorageAccessLayer storage;
	private OutputCommandWriter output;
	
	@Before
	public void setUp() throws ParseException {
		command = new CasServerCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(OutputCommandWriter.class);
		command.setStorage(storage);
		command.channel = output;
		Configuration.INSTANCE.parse(null);
	}
	
	@Test
	public void testShouldThrowExceptionWithTooFewParams() {
		try {
			command.parseCommandParams("terminenzio 12 48 90\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowExceptionWithTooManyParams() {
		try {
			command.parseCommandParams("terminenzio 12 48 90 9999888444 noreply babuu\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldSupportNoReply() {
		assertEquals(5, command.getNoReplyPosition());
	}
	
	@Test
	public void testShouldReturnZeroIfWrongContentLength() throws ErrorException {
		command.parseCommandParams("terminenzio 12 48 cow 7777555 noreply\r\n");
		assertNull(command.payload);
		assertEquals(0, command.payloadContentLength());
	}
	
	@Test
	public void testSholdReturnValidContentLength() throws ErrorException {
		command.parseCommandParams("terminenzio 12 48 90 88844477 noreply\r\n");
		assertNotNull(command.payload);
		assertEquals(90, command.payload.length);
		assertEquals(92, command.payloadContentLength());
		assertEquals(0, command.payloadCursor);
	}
	
	@Test
	public void testAddMultipleParts() throws ErrorException{
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		assertEquals(0, command.payloadCursor);
		for(int i = 1; i < 5; i++){
			command.addPayloadContentPart("0123456789".getBytes());
			assertEquals(10*i, command.payloadCursor);
			command.addPayloadContentPart("".getBytes());  //should discard this
		}
		//Now it is filled - discard other add
		command.addPayloadContentPart("0123456789".getBytes());
		assertEquals(50, command.payloadCursor);
		
		assertEquals("01234567890123456789012345678901234567890123456789", new String(command.payload));
	}
	
	@Test
	public void testAddTooMuchData() throws ErrorException{
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		assertEquals(0, command.payloadCursor);
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		assertEquals(50, command.payloadCursor);

		//Now it is filled - discard other add
		command.addPayloadContentPart("0123456789".getBytes());
		assertEquals(50, command.payloadCursor);
		
		assertEquals("01234567890123456789012345678901234567890123456789", new String(command.payload));
	}
	
	@Test
	public void testInvalidContentPayload() throws ErrorException{
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		assertEquals(0, command.payloadCursor);
		command.addPayloadContentPart("".getBytes());
		assertEquals(0, command.payloadCursor);
		command.addPayloadContentPart(null);
		assertEquals(0, command.payloadCursor);
		command.addPayloadContentPart(new byte[0]);
		assertEquals(0, command.payloadCursor);
	}
	
	@Test 
	public void testCasRainbow() throws ErrorException, StorageException, NotFoundException, ExistsException {
		command.parseCommandParams("terminenzio 12 1252101098 50 35345345345 noreply\r\n");
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		assertEquals(50, command.payloadCursor);

		doAnswer(new Answer<Item>() {

			public Item answer(InvocationOnMock invocation) throws Throwable {

				Item it = (Item) invocation.getArguments()[0];
				assertNotNull(it);
				assertEquals(new Key("terminenzio"), it.getKey());
				assertEquals(12, it.getFlags());
				assertEquals(1252101098, it.getExpire());
				assertEquals("01234567890123456789012345678901234567890123456789", new String(it.getData()));
				assertEquals("35345345345", (String)invocation.getArguments()[1]);
				
				return null;
			}
			
		}).when(storage).swap((Item) anyObject(), (String) eq("35345345345"));
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("STORED\r\n");
	}
	
	@Test 
	public void testCasExceptionOnStorage() throws ErrorException, StorageException, NotFoundException, ExistsException {
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		assertEquals(50, command.payloadCursor);
		
		CrappyDBException exception = new StorageException("BOOM!");
		doThrow(exception).when(storage).swap((Item) anyObject(), (String) eq("35345345345"));
		
		command.execCommand();
		
		verify(storage, times(1)).swap((Item) anyObject(), (String) eq("35345345345"));
		verify(output, times(1)).writeException(exception);
	}
	
	@Test 
	public void testCasNotFoundExceptionOnStorage() throws ErrorException, StorageException, NotFoundException, ExistsException {
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		assertEquals(50, command.payloadCursor);
		
		CrappyDBException exception = new NotFoundException();
		doThrow(exception).when(storage).swap((Item) anyObject(), (String) eq("35345345345"));
		
		command.execCommand();
		
		verify(storage, times(1)).swap((Item) anyObject(), (String) eq("35345345345"));
		verify(output, times(1)).writeException(exception);
	}
	
	@Test 
	public void testCasExistsExceptionOnStorage() throws ErrorException, StorageException, NotFoundException, ExistsException {
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		assertEquals(50, command.payloadCursor);
		
		CrappyDBException exception = new ExistsException();
		doThrow(exception).when(storage).swap((Item) anyObject(), (String) eq("35345345345"));
		
		command.execCommand();
		
		verify(storage, times(1)).swap((Item) anyObject(), (String) eq("35345345345"));
		verify(output, times(1)).writeException(exception);
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("terminenzio 12 48 50 35345345345 noreply\r\n");
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		
		assertEquals("{Cas key=terminenzio flags=12 expire=48 nbytes=50 noreply=true tid=35345345345} {MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODk=}", command.toString());
	}

	@Test
	public void testToStringNoReply() throws ErrorException {
		command.parseCommandParams("terminenzio 12 48 50 35345345345\r\n");
		command.addPayloadContentPart("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789".getBytes());
		
		assertEquals("{Cas key=terminenzio flags=12 expire=48 nbytes=50 noreply=false tid=35345345345} {MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODk=}", command.toString());
	}
	
	@Test
	public void testToStringNoReplyNoPayload() throws ErrorException {
		command.parseCommandParams("terminenzio 12 48 50 35345345345\r\n");
		
		assertEquals("{Cas key=terminenzio flags=12 expire=48 nbytes=50 noreply=false tid=35345345345}", command.toString());
	}

}
