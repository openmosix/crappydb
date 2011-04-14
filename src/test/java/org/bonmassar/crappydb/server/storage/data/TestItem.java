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

package org.bonmassar.crappydb.server.storage.data;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestItem extends TestCase {

	private static class MockItem extends Item {

		public static long now = 1252101098L;
		
		private class MockTimestamp extends Timestamp {

			
			public MockTimestamp(long timestamp) {
				super(timestamp);
			}
			
			@Override
			public long now() {
				return now;
			}
		}
		
		public MockItem(Key storagekey, byte[] data, int flags) {
			super(storagekey, data, flags);
		}
		
		public MockItem(Key storagekey, byte[] data, int flags, long expire) {
			super(storagekey, data, flags, expire);
		}
		
		@Override
		protected Timestamp getTimestamp(long expire){
			return new MockTimestamp(expire);
		}
	}
	
	private MockItem item;
	
	@Before
	public void setUp() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122);
	}
	
	@Test
	public void testShouldThrowANPE() {
		try{
			item = new MockItem(null, "somedata".getBytes(), 122);
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}
	
	@Test
	public void testWithAbsoluteTime() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 1267739498L);
		assertEquals(1267739498L, item.getExpire());
	}
	
	@Test
	public void testWithRelativeTime() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 190L);
		assertEquals(1252101288L, item.getExpire());
	}
	
	@Test
	public void testWithNullTime() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 0L);
		assertEquals(0L, item.getExpire());
	}
	
	@Test
	public void testWithNegativeTime() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, -20L);
		assertEquals(0L, item.getExpire());
	}
	
	@Test
	public void testWithThresholdTime() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 30*24*60*60L);
		assertEquals(1254693098L, item.getExpire());
	}
	
	@Test
	public void testWithThresholdTimePlus1() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 30*24*60*60L+1);
		assertEquals(2592001L, item.getExpire());
	}
	
	@Test
	public void testShouldBeExpired() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 190L);
		MockItem.now = 1252101290L;
		
		assertTrue(item.isExpired());
	}
	
	@Test
	public void testShouldNotBeExpired() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 190L);
		MockItem.now = 1252101286L;
		
		assertFalse(item.isExpired());		
	}
	
	@Test
	public void testShouldNeverExpire() {
		item = new MockItem(new Key("terminenzio"), "somepayload".getBytes(), 122, 0L);
		
		assertFalse(item.isExpired());
	}
}
