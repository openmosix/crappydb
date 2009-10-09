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
 *  
 * The original version of this class comes from Christian d'Heureuse, 
 * Inventec Informatik AG, Switzerland.
 * Home page: http://www.source-code.biz
 * Source code http://www.source-code.biz/snippets/java/2.htm
 *  
 */

package org.bonmassar.crappydb.server.storage.berkley.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public class TestItemEntity {

	@Test
	public void testInjectionConstructor() {
		new ItemEntity();	/*This won't compile if I forget it :) */
	}
	
	@Test
	public void testNullItem() {
		try{
			new ItemEntity(null);
		}catch (NullPointerException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testRainbow() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), "blabla".getBytes(), 18, 2201806658L));
		assertEquals(2201806658L , it.getExpiration());
		assertEquals("terminenzio", it.getPrimaryKey());
		assertEquals(18, it.getFlags());
	}
	
	@Test
	public void testNoFlags() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), "blabla".getBytes(), 0, 2201806658L));
		assertEquals(2201806658L , it.getExpiration());
		assertEquals("terminenzio", it.getPrimaryKey());
		assertEquals(0L, it.getFlags());
	}
	
	@Test
	public void testNoExpiration() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), "blabla".getBytes(), 18));
		assertEquals(0L , it.getExpiration());
		assertEquals("terminenzio", it.getPrimaryKey());
		assertEquals(18, it.getFlags());		
	}
	
	@Test
	public void testNoPayload() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), null, 18, 2201806658L));
		assertEquals(2201806658L , it.getExpiration());
		assertEquals("terminenzio", it.getPrimaryKey());
		assertEquals(18, it.getFlags());				
	}
	
	@Test
	public void testToItemRainbow() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), "blabla".getBytes(), 18, 2201806658L));
		Item back = it.toItem();
		assertEquals(2201806658L, back.getExpire());
		assertEquals(18, back.getFlags());
		assertEquals(new Key("terminenzio"), back.getKey());
		assertTrue(Arrays.equals("blabla".getBytes(), back.getData()));
	}
	
	@Test
	public void testToItemNoFlags() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), "blabla".getBytes(), 0, 2201806658L));
		Item back = it.toItem();
		assertEquals(2201806658L, back.getExpire());
		assertEquals(0L, back.getFlags());
		assertEquals(new Key("terminenzio"), back.getKey());
		assertTrue(Arrays.equals("blabla".getBytes(), back.getData()));
	}
	
	@Test
	public void testToItemNoExpiration() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), "blabla".getBytes(), 18));
		Item back = it.toItem();
		assertEquals(0L, back.getExpire());
		assertEquals(18, back.getFlags());
		assertEquals(new Key("terminenzio"), back.getKey());
		assertTrue(Arrays.equals("blabla".getBytes(), back.getData()));		
	}
	
	@Test
	public void testToItemNoPayload() {
		ItemEntity it = new ItemEntity(new Item(new Key("terminenzio"), null, 18, 2201806658L));
		Item back = it.toItem();
		assertEquals(2201806658L, back.getExpire());
		assertEquals(18, back.getFlags());
		assertEquals(new Key("terminenzio"), back.getKey());
		assertNull( back.getData() );
	}
	
}
