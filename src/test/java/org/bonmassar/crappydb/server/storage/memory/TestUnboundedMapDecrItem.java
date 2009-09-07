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

import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestUnboundedMapDecrItem extends TestCase {

	private UnboundedMap um;
	
	@Before
	public void setUp(){
		um = new UnboundedMap();
	}
	
	@Test
	public void testNullKey() {
		try {
			um.decrease(null, null);
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
			um.decrease(new Key("terminenzio"), null);
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
			um.decrease(new Key("terminenzio"), "180");
		} catch (NotFoundException e) {
			assertEquals("NOT_FOUND", e.clientResponse());
		} catch (StorageException e) {
			fail();
		}
	}
	
	@Test
	public void testDecrementBaseNull() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item (new Key("terminenzio"), null, 0);
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "42");
		assertEquals("0", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("0", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementBaseEmpty() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item (new Key("terminenzio"), new byte[0], 0);
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "42");
		assertEquals("0", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("0", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementBaseInvalid() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item (new Key("terminenzio"), "mucca".getBytes(), 0);
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "42");
		assertEquals("0", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("0", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementZero() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "0");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementNegative() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "-10");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testIncrementWithMucca() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "mucca");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementWithBigNumber() throws NotStoredException, StorageException, NotFoundException{
		Item it = new Item (new Key("terminenzio"), "100000042".getBytes(), 0);
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "100000000");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementWithOverflow() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "18446744073709551610");
		assertEquals("0", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("0", new String(resp.getData()));
	}
	
	@Test
	public void testDecrementWithCompleteOverflow() throws NotStoredException, StorageException, NotFoundException{
		Item it = getDataToAdd();
		um.add(it);
		Item resp = um.decrease(new Key("terminenzio"), "18446744073709551616");
		assertEquals("42", new String( um.get(Arrays.asList(new Key("terminenzio"))).get(0).getData() ));
		assertEquals("42", new String(resp.getData()));
	}
	
	private Item getDataToAdd(){
		Key k = new Key("terminenzio");
		Item it = new Item (k, "42".getBytes(), 0);
		return it;
	}
}
