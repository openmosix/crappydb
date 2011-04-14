
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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.persist.EntityStore;

public class TestBerkleyClose {
	private DBBuilderHelper builder;
	private StorageAccessLayer um;

	@Before
	public void setUp() throws ParseException, StorageException{
		builder = new DBBuilderHelper();
		um = builder.build(); 
	}

	@After
	public void tearDown() throws StorageException {
		builder.clean();
	}

	@Test
	public void testCloseException() throws DatabaseException {
		um = new BerkleyPAL(mockExceptionOnStoreAndEnvironmentClose());
		um.close();	//should not blow
	}
		
	@SuppressWarnings("unchecked")	//mock because Mockito does not mock that method...
	private EntityStore mockExceptionOnStoreAndEnvironmentClose() throws DatabaseException {
		EntityStore store = mock(EntityStore.class);
		doReturn(null).when(store).getPrimaryIndex(String.class, ItemEntity.class);
		Environment env = mock(Environment.class);
		doReturn(env).when(store).getEnvironment();
		doThrow(new DatabaseException()).when(store).close();
		doThrow(new DatabaseException()).when(env).close();
		return store;
	}
	
}
