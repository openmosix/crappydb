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

package org.bonmassar.crappydb.server.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public abstract class TestDeleteItem {

	protected StorageAccessLayer um;
	
	@Test
	public void testNullObject() {
		try {
			um.delete(null, null);
			fail();
		} catch (StorageException e) {
			assertEquals("StorageException [No valid id]", e.toString());
		} catch (NotFoundException e) {
			fail();
		}
	}
	
	@Test 
	public void getInvalidKey() throws NotStoredException, StorageException {
		preloadRepository();
		try {
			um.delete(new Key("Miao"), null);
			fail();
		} catch (NotFoundException e) {
			assertEquals("NOT_FOUND", e.clientResponse());
		} catch (StorageException  e) {
			fail();
		}
	}
	
	@Test 
	public void getOneId() throws NotStoredException, StorageException, NotFoundException {
		preloadRepository();
		um.delete(new Key("Zuu"), null);
		
		assertEquals(0, um.get(Arrays.asList(new Key("Zuu"))).size());
	}
	
	@Test
	public void testDeleteWithTime() throws NotStoredException, StorageException, NotFoundException {
		preloadRepository();
		um.delete(new Key("Zuu"), Long.valueOf(300L));
		
		assertEquals(0, um.get(Arrays.asList(new Key("Zuu"))).size());
	}
	
	protected void preloadRepository() throws NotStoredException, StorageException {
		um.add(getDataToAdd("Muu"));
		um.add(getDataToAdd("Boo"));
		um.add(getDataToAdd("Zuu"));
		um.add(getDataToAdd("Roo"));
		um.add(getDataToAdd("Too"));
	}
	
	private Item getDataToAdd(String key){
		Key k = new Key(key);
		Item it = new Item (k, "some data".getBytes(), 0);
		return it;
	}
	
}
