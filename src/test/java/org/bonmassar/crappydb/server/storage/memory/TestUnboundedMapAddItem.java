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

package org.bonmassar.crappydb.server.storage.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Cas;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapAddItem {

	private UnboundedMap um;
	
	@Before
	public void setUp(){
		um = new UnboundedMap();
	}
	
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
			assertEquals(1, um.repository.size());
			assertEquals(it, um.repository.get(it.getKey()));
			assertNotNull(um.repository.get(it.getKey()).getCas());
			assertTrue(um.repository.get(it.getKey()).getCas().toString().length() > 0);
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
			assertEquals("NotStoredException [Data already exists for this key]", e.toString());
		}
	}
	
	private Item getDataToAdd(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, "some data".getBytes());
		it.setCas(new Cas(1234L));
		return it;
	}
}
