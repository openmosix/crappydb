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

import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Cas;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
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
	
	@Test
	public void testGetMultipleKeysNoCasSomeData() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio terminenzio2");
		
		doAnswer(new Answer<List<Item>>() {
			public List<Item> answer(InvocationOnMock invocation)
					throws Throwable {
				
				@SuppressWarnings(value={"unchecked"})
				List<Key> input = (List<Key>)(invocation.getArguments()[0]);
				assertEquals(2, input.size());
				assertEquals(new Key("terminenzio"), input.get(0));
				assertEquals(new Key("terminenzio2"), input.get(1));
				return Arrays.asList(new Item(new Key("terminenzio"), "this is some data".getBytes(), 12 ));
			}
			
		}).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio 12 17\r\n");
		verify(output, times(1)).writeToOutstanding("this is some data".getBytes());
		verify(output, times(1)).writeToOutstanding("\r\n");
		verify(output, times(1)).writeToOutstanding("END\r\n");
	}
	
	@Test
	public void testGetMultipleKeysNoCasMultipleData() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio terminenzio2");
		
		doAnswer(new Answer<List<Item>>() {
			public List<Item> answer(InvocationOnMock invocation)
					throws Throwable {
				
				@SuppressWarnings(value={"unchecked"})
				List<Key> input = (List<Key>)(invocation.getArguments()[0]);
				assertEquals(2, input.size());
				assertEquals(new Key("terminenzio"), input.get(0));
				assertEquals(new Key("terminenzio2"), input.get(1));
				return Arrays.asList(new Item(new Key("terminenzio"), "this is some data".getBytes(), 12 ),
						new Item(new Key("terminenzio2"), "this is other data".getBytes(), 80 ));
			}
			
		}).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio 12 17\r\n");
		verify(output, times(1)).writeToOutstanding("this is some data".getBytes());
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio2 80 18\r\n");
		verify(output, times(1)).writeToOutstanding("this is other data".getBytes());
		verify(output, times(2)).writeToOutstanding("\r\n");
		verify(output, times(1)).writeToOutstanding("END\r\n");
	}
	
	@Test
	public void testGetMultipleKeysWithCasMultipleData() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio terminenzio2");
		
		doAnswer(new Answer<List<Item>>() {
			public List<Item> answer(InvocationOnMock invocation)
					throws Throwable {
				
				@SuppressWarnings(value={"unchecked"})
				List<Key> input = (List<Key>)(invocation.getArguments()[0]);
				assertEquals(2, input.size());
				assertEquals(new Key("terminenzio"), input.get(0));
				assertEquals(new Key("terminenzio2"), input.get(1));
				Item it1 = new Item(new Key("terminenzio"), "this is some data".getBytes(), 12 );
				it1.setCas(new Cas(1539L));
				Item it2 = new Item(new Key("terminenzio2"), "this is other data".getBytes(), 80 );
				it2.setCas(new Cas(8924L));
				return Arrays.asList(it1, it2);
			}
			
		}).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio 12 17 1539\r\n");
		verify(output, times(1)).writeToOutstanding("this is some data".getBytes());
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio2 80 18 8924\r\n");
		verify(output, times(1)).writeToOutstanding("this is other data".getBytes());
		verify(output, times(2)).writeToOutstanding("\r\n");
		verify(output, times(1)).writeToOutstanding("END\r\n");
	}
	
	@Test
	public void testGetMultipleKeysWithCasNullData() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio terminenzio2");
		
		doAnswer(new Answer<List<Item>>() {
			public List<Item> answer(InvocationOnMock invocation)
					throws Throwable {
				
				@SuppressWarnings(value={"unchecked"})
				List<Key> input = (List<Key>)(invocation.getArguments()[0]);
				assertEquals(2, input.size());
				assertEquals(new Key("terminenzio"), input.get(0));
				assertEquals(new Key("terminenzio2"), input.get(1));
				Item it1 = new Item(new Key("terminenzio"), null, 12 );
				it1.setCas(new Cas(1539L));
				Item it2 = new Item(new Key("terminenzio2"), null, 80 );
				it2.setCas(new Cas(8924L));
				return Arrays.asList(it1, it2);
			}
			
		}).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio 12 0 1539\r\n");
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio2 80 0 8924\r\n");
		verify(output, times(2)).writeToOutstanding("\r\n");
		verify(output, times(1)).writeToOutstanding("END\r\n");
	}
	
	@Test
	public void testGetMultipleKeysWithCasNoData() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio terminenzio2");
		
		doAnswer(new Answer<List<Item>>() {
			public List<Item> answer(InvocationOnMock invocation)
					throws Throwable {
				
				@SuppressWarnings(value={"unchecked"})
				List<Key> input = (List<Key>)(invocation.getArguments()[0]);
				assertEquals(2, input.size());
				assertEquals(new Key("terminenzio"), input.get(0));
				assertEquals(new Key("terminenzio2"), input.get(1));
				Item it1 = new Item(new Key("terminenzio"), new byte[0], 12 );
				it1.setCas(new Cas(1539L));
				Item it2 = new Item(new Key("terminenzio2"), new byte[0], 80 );
				it2.setCas(new Cas(8924L));
				return Arrays.asList(it1, it2);
			}
			
		}).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio 12 0 1539\r\n");
		verify(output, times(1)).writeToOutstanding("VALUE terminenzio2 80 0 8924\r\n");
		verify(output, times(2)).writeToOutstanding("\r\n");
		verify(output, times(1)).writeToOutstanding("END\r\n");
	}
	
	@Test
	public void testGetMultipleKeysErrorAccessingStorage() throws ErrorException, StorageException {
		command.parseCommandParams("terminenzio terminenzio2");
		
		CrappyDBException exception = new StorageException("BOOM!");
		doThrow(exception).when(storage).get((List<Key>)anyList());
		
		command.execCommand();
		verify(output, times(1)).writeException(exception);
	}
}
