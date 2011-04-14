
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

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.TestExpiration;
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

public class TestBerkleyExpiration extends TestExpiration{
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
	public void testExceptionOnGet() throws DatabaseException {
		um = new BerkleyPAL(mockExceptionOnGet());
		Item result = um.expire(new Key("Zuuu"));
		assertNull(result);
	}
	
	@Test
	public void testExceptionOnCommit() throws DatabaseException {
		um = new BerkleyPAL(mockExceptionOnCommit());
		Item result = um.expire(new Key("Zuuu"));
		assertNull(result);
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
	private EntityStore mockExceptionOnCommit() throws DatabaseException {
		final HelperPair pair = builder.createSettingForMock();
		class PrimaryIndexExceptionOnEntities extends PrimaryIndex<String, ItemEntity>{
			PrimaryIndexExceptionOnEntities() throws DatabaseException {
				super(new Environment(new File(HelperPair.dbpath), pair.envConfig).openDatabase(null, "tryppy", pair.dbConfig), 
						String.class, null, ItemEntity.class, null);
			}
		}
		
		EntityStore store = mock(EntityStore.class);
		doReturn(new PrimaryIndexExceptionOnEntities()).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		Environment env = mock(Environment.class);
		doReturn(env).when(store).getEnvironment();
		Transaction txn = mock(Transaction.class);
		doReturn(txn).when(env).beginTransaction(null, null);
		doThrow(new DatabaseException()).when(txn).commit();
		return store;
	}
}
