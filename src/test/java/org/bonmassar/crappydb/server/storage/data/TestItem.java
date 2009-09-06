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
		
		public long now = 1252101098L;
		
		private class MockTimestamp extends Timestamp {

			public MockTimestamp(long timestamp) {
				super(timestamp);
				// TODO Auto-generated constructor stub
			}
			
			@Override
			public long now() {
				return now;
			}
		}
		
		public MockItem(Key storagekey, byte[] data, int flags) {
			super(storagekey, data, flags);
		}
		
		@Override
		public void setExpire(long newexpire) {
			expire = new MockTimestamp(newexpire);
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
		item.setExpire(1267739498L);
		assertEquals(1267739498L, item.getExpire());
	}
	
	@Test
	public void testWithRelativeTime() {
		item.setExpire(190L);
		assertEquals(1252101288L, item.getExpire());
	}
	
	@Test
	public void testWithNullTime() {
		item.setExpire(0L);
		assertEquals(0L, item.getExpire());
	}
	
	@Test
	public void testWithNegativeTime() {
		item.setExpire(-20L);
		assertEquals(0L, item.getExpire());
	}
	
	@Test
	public void testWithThresholdTime() {
		item.setExpire(30*24*60*60);
		assertEquals(1254693098L, item.getExpire());
	}
	
	@Test
	public void testWithThresholdTimePlus1() {
		item.setExpire(30*24*60*60+1);
		assertEquals(2592001L, item.getExpire());
	}
	
	@Test
	public void testShouldBeExpired() {
		item.setExpire(190L);
		item.now = 1252101290L;
		
		assertTrue(item.isExpired());
	}
	
	@Test
	public void testShouldNotBeExpired() {
		item.setExpire(190L);
		item.now = 1252101286L;
		
		assertFalse(item.isExpired());		
	}
	
	@Test
	public void testShouldNeverExpire() {
		item.setExpire(0L);
		
		assertFalse(item.isExpired());
	}
	
}
