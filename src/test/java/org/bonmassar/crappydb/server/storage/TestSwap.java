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

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public abstract class TestSwap {
	protected StorageAccessLayer um;
	private Item previt;

	public void setUp() throws StorageException, ParseException{
		previt = new Item(new Key("Terminenzio"), "This is the payload.".getBytes(), 22, 1979072581L);
		um.set( previt );
	}
	
	@Test
	public void testNullObject() {
		try {
			um.swap(null, "19822");
		} catch (StorageException e) {
			assertEquals("StorageException [Null item]", e.toString());
		} catch (NotFoundException e) {
			fail();
		} catch (ExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testEmptyCasId() {
		try {
			um.swap(new Item(new Key("T"), "".getBytes(), 0), "");
		} catch (StorageException e) {
			assertEquals("StorageException [No CAS]", e.toString());
		} catch (NotFoundException e) {
			fail();
		} catch (ExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testNullCasId() {
		try {
			um.swap(new Item(new Key("T"), "".getBytes(), 0), null);
		} catch (StorageException e) {
			assertEquals("StorageException [No CAS]", e.toString());
		} catch (NotFoundException e) {
			fail();
		} catch (ExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testItemNotFound() {
		Item it = new Item(new Key("terminenzio"), "some payload".getBytes(), 90);
		try {
			um.swap(it, "11776555714");
			fail();
		} catch (NotFoundException e) {
			assertEquals("NOT_FOUND", e.clientResponse());
		} catch (ExistsException e) {
			fail();
		} catch (StorageException e) {
			fail();
		}
	}
	
	@Test
	public void testRainbow() throws NotFoundException, ExistsException, StorageException {
		Item it = new Item(new Key("Terminenzio"), "new payload".getBytes(), 88, 2010608581L);
		um.swap(it, "11776555714");
		
		assertEquals("new payload", new String(um.get(Arrays.asList(new Key("Terminenzio"))).get(0).getData()));
		assertEquals(88, um.get(Arrays.asList(new Key("Terminenzio"))).get(0).getFlags());
		assertEquals(2010608581L, um.get(Arrays.asList(new Key("Terminenzio"))).get(0).getExpire());
	}
	
	@Test
	public void testChangedPayload() throws NotFoundException, StorageException {
		Item it = new Item(new Key("Terminenzio"), "new payload".getBytes(), 88, 6666L);
		
		um.set(new Item(previt.getKey(), "other payload".getBytes(), previt.getFlags(), 1979072581L));
		
		try {
			um.swap(it, "11776555714");
			fail();
		} catch (ExistsException e) {
			return;
		}
	}
	
	@Test
	public void testChangedExpiration() throws NotFoundException, StorageException {
		Item it = new Item(new Key("Terminenzio"), "new payload".getBytes(), 88, 6666);
		
		Item newit = new Item(previt.getKey(), previt.getData(), 999998, 1979072281L);
		um.set(newit);
		
		try {
			um.swap(it, "11776555714");
			fail();
		} catch (ExistsException e) {
			return;
		}
	}
	
	@Test
	public void testChangedFlags() throws NotFoundException, StorageException {
		Item it = new Item(new Key("Terminenzio"), "new payload".getBytes(), 88, 6666);
		
		um.set(new Item(previt.getKey(), previt.getData(), 999998, 1979072581L));
		
		try {
			um.swap(it, "11776555714");
			fail();
		} catch (ExistsException e) {
			return;
		}
	}
}
