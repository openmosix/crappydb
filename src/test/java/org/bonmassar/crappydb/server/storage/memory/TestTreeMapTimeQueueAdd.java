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

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestTreeMapTimeQueueAdd {

	private TreeMapTimeQueue queue;
	private Item item;
	private Item item2;
	private Key key;
	private Key key2;
	
	@Before
	public void setUp() {
		queue = new TreeMapTimeQueue();
		key = new Key("terminenzio");
		key2 = new Key("terminenzio2");
		item = new Item(key, new String("gioconno"));
		item2 = new Item(key2, new String("gioconno2"));
	}
	
	@Test
	public void testAddWithNull(){
		queue.add(null);
		assertEquals(0, queue.queue.size());
	}
	
	@Test
	public void testAddWithNoExpiration(){
		queue.add(item);
		assertEquals(0, queue.queue.size());
	}
	
	@Test
	public void testAddWithOneExpiration(){
		item.setExpire(999L);
		queue.add(item);
		assertEquals(1, queue.queue.size());
	}
	
	@Test
	public void testAddWithTwoExpirationsSameExpiration(){
		item.setExpire(999L);
		item2.setExpire(999L);
		queue.add(item).add(item2);
		assertEquals(1, queue.queue.size());
	}
	
	@Test
	public void testAddWithTwoExpirationsDifferentExpiration(){
		item.setExpire(999L);
		item2.setExpire(998L);
		queue.add(item).add(item2);
		assertEquals(2, queue.queue.size());
	}
	
	
}
