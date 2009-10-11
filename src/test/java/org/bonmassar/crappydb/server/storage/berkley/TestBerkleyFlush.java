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
 *  
 * The original version of this class comes from Christian d'Heureuse, 
 * Inventec Informatik AG, Switzerland.
 * Home page: http://www.source-code.biz
 * Source code http://www.source-code.biz/snippets/java/2.htm
 *  
 */

package org.bonmassar.crappydb.server.storage.berkley;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.TestFlush;
import org.bonmassar.crappydb.server.storage.berkley.DBBuilderHelper.HelperPair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class TestBerkleyFlush extends TestFlush {
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
	public void testExceptionOnFlush() throws DatabaseException {
		um = new BerkleyPAL(mockExceptionOnEntities());
		try{
			um.flush(Long.valueOf(0));
		}
		catch(StorageException e){
			return;
		}
		fail();
	}

	@Test
	public void testExceptionOnCursor() throws DatabaseException {
		um = new BerkleyPAL(mockExceptionOnCursor());
		try{
			um.flush(Long.valueOf(0));
		}
		catch(StorageException e){
			return;
		}
		fail();
	}
	
	@SuppressWarnings("unchecked")	//mock because Mockito does not mock that method...
	private EntityStore mockExceptionOnEntities() throws DatabaseException {
		final HelperPair pair = builder.createSettingForMock();
		class PrimaryIndexExceptionOnEntities extends PrimaryIndex<String, ItemEntity>{
			PrimaryIndexExceptionOnEntities() throws DatabaseException {
				super(new Environment(new File(HelperPair.dbpath), pair.envConfig).openDatabase(null, "tryppy", pair.dbConfig), 
						String.class, null, ItemEntity.class, null);
			}
			
			@Override
			public EntityCursor<ItemEntity> entities(Transaction txn,
					CursorConfig config) throws DatabaseException {
				throw new DatabaseException();
			}
		}
		
		EntityStore store = mock(EntityStore.class);
		doReturn(new PrimaryIndexExceptionOnEntities()).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		doReturn(mock(Environment.class)).when(store).getEnvironment();
		return store;
	}
	
	@SuppressWarnings("unchecked")	//mock because Mockito does not mock that method...
	private EntityStore mockExceptionOnCursor() throws DatabaseException {
		final HelperPair pair = builder.createSettingForMock();
		class PrimaryIndexExceptionOnEntities extends PrimaryIndex<String, ItemEntity>{
			PrimaryIndexExceptionOnEntities() throws DatabaseException {
				super(new Environment(new File(HelperPair.dbpath), pair.envConfig).openDatabase(null, "tryppy", pair.dbConfig), 
						String.class, null, ItemEntity.class, null);
			}
			
			@Override
			public EntityCursor<ItemEntity> entities(Transaction txn,
					CursorConfig config) throws DatabaseException {
				EntityCursor<ItemEntity> cursor = mock(EntityCursor.class);
				doThrow(new DatabaseException()).when(cursor).first();
				return cursor;
			}
		}
		
		EntityStore store = mock(EntityStore.class);
		doReturn(new PrimaryIndexExceptionOnEntities()).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		doReturn(mock(Environment.class)).when(store).getEnvironment();
		return store;
	}
}
