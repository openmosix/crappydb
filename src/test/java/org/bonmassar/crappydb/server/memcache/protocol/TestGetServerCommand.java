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

import java.util.Arrays;
import java.util.List;

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.anyList;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
	
	@Test
	public void testGetOneKeyNoCasSomeData() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio");
		
		doAnswer(new Answer<List<Item>>() {
			public List<Item> answer(InvocationOnMock invocation)
					throws Throwable {
				
				@SuppressWarnings(value={"unchecked"})
				List<Key> input = (List<Key>)(invocation.getArguments()[0]);
				assertEquals(1, input.size());
				assertEquals(new Key("terminenzio"), input.get(0));
				return Arrays.asList(new Item(new Key("terminenzio"), "this is some data".getBytes(), 12 ));
			}
			
		}).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio 12 17\r\n");
		verify(output, times(1)).writeToOutstanding("this is some data".getBytes());
		verify(output, times(1)).writeToOutstanding("\r\n");
		verify(output, times(1)).writeToOutstanding("END\r\n");
	}
}
