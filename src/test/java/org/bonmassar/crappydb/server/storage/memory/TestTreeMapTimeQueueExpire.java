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

import java.util.List;

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;


public class TestTreeMapTimeQueueExpire  extends TestCase {

	class MockTreeMapTimeQueue extends TreeMapTimeQueue{
		@Override
		protected Long getNow() {
			return 1242922797L;
		}
	}
	
	private TreeMapTimeQueue queue;
	private Item item1, item2, item3, item4, item5, item6;
	private Key key1, key2, key3, key4, key5, key6;
	
	@Before
	public void setUp() {
		queue = new MockTreeMapTimeQueue();
		stubItems();
		queue.add(item1).add(item2).add(item3).add(item4).add(item5).add(item6);
	}
	
	@Test
	public void testExpireNow() {
		List<Key> keys = queue.expireNow();
		assertNotNull(keys);
		assertEquals(5, keys.size());
		assertEquals(1, queue.queue.size());
		
		assertEquals(key2, queue.queue.get(1242922798L).get(0));
		
		assertTrue(keys.contains(key1));
		assertFalse(keys.contains(key2));
		assertTrue(keys.contains(key3));
		assertTrue(keys.contains(key4));
		assertTrue(keys.contains(key5));
		assertTrue(keys.contains(key6));
	}
	
	private void stubItems(){
		key1 = new Key("terminenzio");
		key2 = new Key("terminenzio2");
		key3 = new Key("terminenzio3");
		key4 = new Key("terminenzio4");
		key5 = new Key("terminenzio5");
		key6 = new Key("terminenzio6");
		item1 = new Item(key1, new String("gioconno").getBytes());
		item1.setExpire(1242922797L);
		item2 = new Item(key2, new String("gioconno2").getBytes());
		item2.setExpire(1242922798L);
		item3 = new Item(key3, new String("gioconno3").getBytes());
		item3.setExpire(1242922797L);
		item4 = new Item(key4, new String("gioconno4").getBytes());
		item4.setExpire(1242922796L);
		item5 = new Item(key5, new String("gioconno5").getBytes());
		item5.setExpire(1242922794L);
		item6 = new Item(key6, new String("gioconno6").getBytes());
		item6.setExpire(1242922794L);
	}

}
