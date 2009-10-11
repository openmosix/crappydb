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

package org.bonmassar.crappydb.server.storage.berkley;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.TestGetItems;
import org.bonmassar.crappydb.server.storage.berkley.DBBuilderHelper.HelperPair;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class TestBerkleyGetItems extends TestGetItems {
	private DBBuilderHelper builder;

	@Before
	public void setUp() throws ParseException{
		builder = new DBBuilderHelper();
		um = builder.build(); 
	}

	@After
	public void tearDown() throws StorageException {
		builder.clean();
	}
	
	@Test
	public void testGetExpiredItem() throws StorageException, InterruptedException {
		um.set(new Item(new Key("Zzz"), "payload".getBytes(), 12, 2));
		Thread.sleep(3000);
		List<Item> result = um.get(Arrays.asList(new Key("Zzz")));
		assertNotNull(result);
		assertEquals(0, result.size());
	}
	
	@Test
	public void testGetExpiredItemExceptionOnDelete() throws StorageException, InterruptedException, DatabaseException {
		um = new BerkleyPAL(mockExceptionOnDelete());
		List<Item> result = um.get(Arrays.asList(new Key("Zzz")));
		assertNotNull(result);
		assertEquals(0, result.size());
	}
	
	@Test
	public void testRuntimeExceptionIsPropagatedButCommit() throws DatabaseException, StorageException {
		um = new BerkleyPAL(mockNullPointerExceptionOnGet());
		try{
			um.get(Arrays.asList(new Key("Zuuu")));
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}
	
	@Test
	public void testDBExceptionOnGet() throws DatabaseException, StorageException {
		um = new BerkleyPAL(mockExceptionOnGet());
		List<Item> result = um.get(Arrays.asList(new Key("Zuuu")));
		assertNotNull(result);
		assertEquals(0, result.size());
	}
	
	@SuppressWarnings("unchecked")	//mock because Mockito does not mock that method...
	private EntityStore mockNullPointerExceptionOnGet() throws DatabaseException {
		EntityStore store = mock(EntityStore.class);
		doReturn(null).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		doReturn(mock(Environment.class)).when(store).getEnvironment();
		return store;
	}
	
	@SuppressWarnings("unchecked")	//mock because Mockito does not mock that method...
	private EntityStore mockExceptionOnGet() throws DatabaseException {
		final HelperPair pair = builder.createSettingForMock();
		class PrimaryIndexExceptionOnEntities extends PrimaryIndex<String, ItemEntity>{
			PrimaryIndexExceptionOnEntities() throws DatabaseException {
				super(new Environment(new File(HelperPair.dbpath), pair.envConfig).openDatabase(null, "tryppy", pair.dbConfig), 
						String.class, null, ItemEntity.class, null);
			}
			
			@Override
			public ItemEntity get(Transaction txn, String key, LockMode lockMode)
					throws DatabaseException {
				throw new DatabaseException();
			}
		}
		
		EntityStore store = mock(EntityStore.class);
		doReturn(new PrimaryIndexExceptionOnEntities()).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		doReturn(mock(Environment.class)).when(store).getEnvironment();
		return store;
	}
	
	@SuppressWarnings("unchecked")	//mock because Mockito does not mock that method...
	private EntityStore mockExceptionOnDelete() throws DatabaseException {
		final HelperPair pair = builder.createSettingForMock();
		class PrimaryIndexExceptionOnEntities extends PrimaryIndex<String, ItemEntity>{
			PrimaryIndexExceptionOnEntities() throws DatabaseException {
				super(new Environment(new File(HelperPair.dbpath), pair.envConfig).openDatabase(null, "tryppy", pair.dbConfig), 
						String.class, null, ItemEntity.class, null);
			}
			
			@Override
			public boolean delete(Transaction txn, String key)
					throws DatabaseException {
				throw new DatabaseException();
			}
			
			@Override
			public ItemEntity get(Transaction txn, String key, LockMode lockMode)
					throws DatabaseException {
				return new ItemEntity(new Item(new Key("Zzz"), "payload".getBytes(), 12, 1002811787));
			}
		}
		
		EntityStore store = mock(EntityStore.class);
		doReturn(new PrimaryIndexExceptionOnEntities()).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		doReturn(mock(Environment.class)).when(store).getEnvironment();
		return store;
	}
}
