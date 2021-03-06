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

public abstract class TestIncr {

	protected StorageAccessLayer um;

	@Test
	public void testNullKey() {
		try {
			um.increase(null, null);
			fail();
		} catch (NotFoundException e) {
			fail();
		} catch (StorageException e) {
			assertEquals("StorageException [No valid id]", e.clientResponse());
		}
	}
	
	@Test
	public void testNullItem() {
		try {
			um.increase(new Key("terminenzio"), null);
			fail();
		} catch (NotFoundException e) {
			fail();
		} catch (StorageException e) {
			assertEquals("StorageException [Null item]", e.clientResponse());
		}
	}
	
	@Test
	public void testKeyNotFound() {
		try {
			um.increase(new Key("terminenzio"), "180");
		} catch (NotFoundException e) {
			assertEquals("NOT_FOUND", e.clientResponse());
		} catch (StorageException e) {
			fail();
		}
	}
	
	@Test
	public void testIncrementBaseNull() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item ( new Key("terminenzio"), null, 0);
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "42");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementBaseEmpty() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item ( new Key("terminenzio"), new byte[0], 0);
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "42");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementBaseInvalid() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item ( new Key("terminenzio"), "mucca".getBytes(), 0);
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "42");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementZero() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "0");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementNegative() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "-10");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementWithMucca() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "mucca");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementWithBigNumber() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "100000000");
		assertEquals("100000042", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("100000042", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementWithOverflow() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "18446744073709551610");
		assertEquals("36", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("36", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementWithCompleteOverflow() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.increase(new Key("terminenzio"), "18446744073709551616");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	private Item getDataToAdd(){
		Key k = new Key("terminenzio");
		Item it = new Item (k, "42".getBytes(), 0);
		return it;
	}
}
