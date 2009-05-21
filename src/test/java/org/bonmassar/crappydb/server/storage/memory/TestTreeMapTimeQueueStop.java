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
import static org.junit.Assert.assertTrue;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestTreeMapTimeQueueStop {

	private TreeMapTimeQueue queue;
	private Item item;
	private Item item2;
	private Item item3;
	private Item item4;
	private Key key;
	private Key key2;
	private Key key3;
	private Key key4;
	
	@Before
	public void setUp() {
		queue = new TreeMapTimeQueue();
		stubItems();
		queue.add(item);
		queue.add(item2);
		queue.add(item3);
		queue.add(item4);
	}
	
	@Test
	public void testKillItem1(){
		queue.stop(item);
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
		assertEquals(key, queue.queue.get(999L).get(0));
	}
	
	@Test
	public void testKillItem3(){
		queue.stop(item3);
		assertTrue(queue.queue.containsKey(997L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(2, queue.queue.get(999L).size());
		assertTrue(queue.queue.get(999L).contains(key));
		assertTrue(queue.queue.get(999L).contains(key));
	}
	
	@Test
	public void testKillItem4(){
		queue.stop(item4);
		assertTrue(queue.queue.containsKey(998L));
		assertTrue(queue.queue.containsKey(999L));
		assertEquals(2, queue.queue.get(999L).size());
		assertTrue(queue.queue.get(999L).contains(key));
		assertTrue(queue.queue.get(999L).contains(key));
	}
	
	private void stubItems(){
		key = new Key("terminenzio");
		key2 = new Key("terminenzio2");
		key3 = new Key("terminenzio3");
		key4 = new Key("terminenzio4");
		item = new Item(key, new String("gioconno"));
		item.setExpire(999L);
		item2 = new Item(key2, new String("gioconno2"));
		item2.setExpire(999L);
		item3 = new Item(key3, new String("gioconno3"));
		item3.setExpire(998L);
		item4 = new Item(key4, new String("gioconno4"));
		item4.setExpire(997L);
	}
}
