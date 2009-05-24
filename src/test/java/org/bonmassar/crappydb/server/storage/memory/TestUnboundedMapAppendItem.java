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
import static org.junit.Assert.fail;

import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Cas;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapAppendItem {
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
		} catch (StorageException e) {
			assertEquals("StorageException [Null item]", e.toString());
		}
	}
	
	@Test
	public void testInvalidKey() {
		try {
			Item it = new Item (null, "some data".getBytes());
			um.append(it);
		} catch (StorageException e) {
			assertEquals("StorageException [Invalid key]", e.toString());
		}
	}
	
	@Test
	public void testKeyNotFound() {
		try {
			Item it = getDataToAdd();
			um.append(it);
		} catch (StorageException e) {
			assertEquals("StorageException [Unknown key]", e.toString());
		}
	}
	
	@Test
	public void testAppendNull() throws NotStoredException, StorageException {
		Item it = getDataToAdd();
		um.add(it);
		Item mit = getDataToAppend();
		mit.setData(null);
		um.append(mit);
		assertEquals("some data", new String( um.repository.get(new Key("Yuppi")).getData() ));
	}
	
	@Test
	public void testAppendSomething() throws NotStoredException, StorageException {
		Item it = getDataToAdd();
		um.add(it);
		Item mit = getDataToAppend();
		um.append(mit);
		assertEquals("some data some other more data", new String( um.repository.get(new Key("Yuppi")).getData() ));
	}
	
	@Test
	public void testAppendWithPreviousNull() throws NotStoredException, StorageException {
		Item it = getDataToAdd();
		it.setData("".getBytes());
		um.add(it);
		Item mit = getDataToAppend();
		um.append(mit);
		assertEquals(" some other more data", new String( um.repository.get(new Key("Yuppi")).getData() ));
	}
	
	private Item getDataToAdd(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, "some data".getBytes());
		it.setCas(new Cas(1234L));
		return it;
	}
	
	private Item getDataToAppend(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, " some other more data".getBytes());
		return it;
	}
}
