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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapGetItems  extends TestCase{
	private UnboundedMap um;
	
	@Before
	public void setUp(){
		um = new UnboundedMap();
	}
	
	@Test
	public void testNullObject() {
		try {
			um.get(null);
			fail();
		} catch (StorageException e) {
			assertEquals("StorageException [No valid ids]", e.toString());
		}
	}
	
	@Test 
	public void getInvalidKey() throws NotStoredException, StorageException {
		preloadRepository();
		try {
			List<Item> found = um.get(Arrays.asList(new Key("Miao")));
			assertNotNull(found);
			assertEquals(0, found.size());
		} catch (StorageException e) {
			fail();
		}
	}
	
	@Test 
	public void getOneId() throws NotStoredException, StorageException, NotFoundException {
		preloadRepository();
		List<Item> found = um.get(Arrays.asList(new Key("Zuu")));
		assertEquals(1, found.size());
		assertEquals(new Key("Zuu"), found.get(0).getKey());
	}
	
	@Test 
	public void getTwoIdButOneFake() throws NotStoredException, StorageException, NotFoundException {
		preloadRepository();
		List<Item> found = um.get(Arrays.asList(new Key("Zulu"), new Key("Zuu")));
		assertEquals(1, found.size());
		assertEquals(new Key("Zuu"), found.get(0).getKey());
	}
	
	@Test 
	public void getThreeIdButTwoFakes() throws NotStoredException, StorageException, NotFoundException {
		preloadRepository();
		List<Item> found = um.get(Arrays.asList(new Key("Too"), null, new Key("Zulu"), new Key("Zuu")));
		assertEquals(2, found.size());
		assertEquals(new Key("Too"), found.get(0).getKey());
		assertEquals(new Key("Zuu"), found.get(1).getKey());
	}

	private void preloadRepository() throws NotStoredException, StorageException {
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
