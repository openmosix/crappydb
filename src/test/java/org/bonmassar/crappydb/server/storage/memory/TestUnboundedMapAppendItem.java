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

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapAppendItem extends TestCase {
	private UnboundedMap um;
	
	@Before
	public void setUp(){
		um = new UnboundedMap();
	}
	
	@Test
	public void testNullObject() {
		try {
			um.append(null);
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
			Item it = getDataToAdd();
			um.append(it);
		} catch (NotFoundException e) {
			assertEquals("NOT_FOUND", e.clientResponse());
		} catch (StorageException e) {
			fail();
		}
	}
	
	@Test
	public void testAppendNull() throws NotStoredException, StorageException, NotFoundException {
		Item it = getDataToAdd();
		um.add(it);
		Item mit = getDataToAppend();
		mit.setData(null);
		um.append(mit);
		assertEquals("some data", new String( um.get(Arrays.asList(new Key("Yuppi"))).get(0).getData() ));
	}
	
	@Test
	public void testAppendSomething() throws NotStoredException, StorageException, NotFoundException {
		Item it = getDataToAdd();
		um.add(it);
		Item mit = getDataToAppend();
		um.append(mit);
		assertEquals("some data some other more data", new String( um.get(Arrays.asList(new Key("Yuppi"))).get(0).getData()  ));
	}
	
	@Test
	public void testAppendWithPreviousNull() throws NotStoredException, StorageException, NotFoundException {
		Item it = getDataToAdd();
		it.setData(null);
		um.add(it);
		Item mit = getDataToAppend();
		um.append(mit);
		assertEquals(" some other more data", new String( um.get(Arrays.asList(new Key("Yuppi"))).get(0).getData()  ));
	}
	
	@Test
	public void testAppendWithPreviousEmpty() throws NotStoredException, StorageException, NotFoundException {
		Item it = getDataToAdd();
		it.setData("".getBytes());
		um.add(it);
		Item mit = getDataToAppend();
		um.append(mit);
		assertEquals(" some other more data", new String( um.get(Arrays.asList(new Key("Yuppi"))).get(0).getData()  ));
	}
	
	private Item getDataToAdd(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, "some data".getBytes(), 0);
		return it;
	}
	
	private Item getDataToAppend(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, " some other more data".getBytes(), 0);
		return it;
	}
}
