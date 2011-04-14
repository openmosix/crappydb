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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public abstract class TestAddItem {

	protected StorageAccessLayer um;

	@Test
	public void testNullObject() {
		try {
			um.add(null);
		} catch (StorageException e) {
			assertEquals("StorageException [Null item]", e.toString());
		} catch (NotStoredException e) {
			fail();
		}
	}
		
	@Test
	public void testKeyAdded() {
		try {
			Item it = getDataToAdd();
			um.add(it);
			assertEquals(it.getExpire(), um.get(Arrays.asList(it.getKey())).get(0).getExpire());
			assertEquals(it.getKey(), um.get(Arrays.asList(it.getKey())).get(0).getKey());
			assertEquals(it.getFlags(), um.get(Arrays.asList(it.getKey())).get(0).getFlags());			
			assertTrue(Arrays.equals(it.getData(), um.get(Arrays.asList(it.getKey())).get(0).getData()));
			assertEquals(it.generateCAS().getValue(), um.get(Arrays.asList(it.getKey())).get(0).generateCAS().getValue());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testFailDataAlreadyExisting() throws NotStoredException, StorageException {
		Item it = getDataToAdd();
		um.add(it);
		
		try {
			um.add(it);
			fail();
		} catch (NotStoredException e) {
			assertEquals("NOT_STORED", e.clientResponse());
		}
	}
	
	private Item getDataToAdd(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, "some data".getBytes(), 0);
		return it;
	}
	
}
