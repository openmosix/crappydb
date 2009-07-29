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

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestTreeMapTimeQueueStop  extends TestCase {

	private TreeMapTimeQueue queue;
	private Item item1, item2, item3, item4;
	private Key key1, key2, key3, key4;
	
	@Before
	public void setUp() {
		queue = new TreeMapTimeQueue();
		stubItems();
		queue.add(item1).add(item2).add(item3).add(item4);
	}

	@Test
	public void testKillItemDoesNotExists(){
		Key fakekey = new Key("does not matter");
		Item notExists = new Item(fakekey, new String("some data").getBytes(), 0);
		queue.stop(notExists);
		assertTrue(queue.queue.containsKey(997L));
		assertTrue(queue.queue.containsKey(998L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(2, queue.queue.get(999L).size());
		assertTrue(queue.queue.get(999L).contains(key1));
		assertTrue(queue.queue.get(999L).contains(key1));
	}

	
	@Test
	public void testKillNull(){
		queue.stop(null);
		assertTrue(queue.queue.containsKey(997L));
		assertTrue(queue.queue.containsKey(998L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(2, queue.queue.get(999L).size());
		assertTrue(queue.queue.get(999L).contains(key1));
		assertTrue(queue.queue.get(999L).contains(key1));
	}
	
	@Test
	public void testKillItem1(){
		queue.stop(item1);
		assertTrue(queue.queue.containsKey(997L));
		assertTrue(queue.queue.containsKey(998L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(1, queue.queue.get(999L).size());
		assertEquals(key2, queue.queue.get(999L).get(0));
	}
	
	@Test
	public void testKillItem2(){
		queue.stop(item2);
		assertTrue(queue.queue.containsKey(997L));
		assertTrue(queue.queue.containsKey(998L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(1, queue.queue.get(999L).size());
		assertEquals(key1, queue.queue.get(999L).get(0));
	}
	
	@Test
	public void testKillItem3(){
		queue.stop(item3);
		assertTrue(queue.queue.containsKey(997L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(2, queue.queue.get(999L).size());
		assertTrue(queue.queue.get(999L).contains(key1));
		assertTrue(queue.queue.get(999L).contains(key1));
	}
	
	@Test
	public void testKillItem4(){
		queue.stop(item4);
		assertTrue(queue.queue.containsKey(998L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(2, queue.queue.get(999L).size());
		assertTrue(queue.queue.get(999L).contains(key1));
		assertTrue(queue.queue.get(999L).contains(key1));
	}
	
	private void stubItems(){
		key1 = new Key("terminenzio");
		key2 = new Key("terminenzio2");
		key3 = new Key("terminenzio3");
		key4 = new Key("terminenzio4");
		item1 = new Item(key1, new String("gioconno").getBytes(), 0);
		item1.setExpire(999L);
		item2 = new Item(key2, new String("gioconno2").getBytes(), 0);
		item2.setExpire(999L);
		item3 = new Item(key3, new String("gioconno3").getBytes(), 0);
		item3.setExpire(998L);
		item4 = new Item(key4, new String("gioconno4").getBytes(), 0);
		item4.setExpire(997L);
	}
}
