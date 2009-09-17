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

public class TestTimestamp extends TestCase {
	
	private Timestamp time;
	private long now = 1252101098L;
	
	private class MockTimestamp extends Timestamp {

		public MockTimestamp(long timestamp) {
			super(timestamp);
		}
		
		@Override
		public long now() {
			return now;
		}
	}
	
	@Before
	public void setUp() {
		time = new Timestamp(1252243484L);
	}
	
	@Test
	public void testGetMinTimestampLess() {
		Timestamp t = Timestamp.getMinTimestamp(2010608581, 2010608589);
		assertEquals(2010608581, t.getExpire());
	}
	
	@Test
	public void testGetMinTimestampMore() {
		Timestamp t = Timestamp.getMinTimestamp(2010608589, 2010608581);
		assertEquals(2010608581, t.getExpire());
	}
	
	@Test
	public void testGetMinTimestampEquals() {
		Timestamp t = Timestamp.getMinTimestamp(2010608581, 2010608581);
		assertEquals(2010608581, t.getExpire());
	}
	
	@Test
	public void testGetMinTimestampLessT1Relative() {
		Timestamp t = Timestamp.getMinTimestamp(10, 2010608581);
		assertFalse(2010608581 == t.getExpire());
	}
	
	@Test
	public void testGetMinTimestampMoreT1Relative() {
		Timestamp t = Timestamp.getMinTimestamp(2010608581, 10 );
		assertFalse(2010608581 == t.getExpire());
	}
			
	@Test
	public void testGetMinTimestampLessBothRelative() {
		Timestamp t = Timestamp.getMinTimestamp(1000000, 1000 );
		assertFalse((System.currentTimeMillis() / 1000 + 1000) < t.getExpire());
	}
	
	@Test
	public void testTimestampsAreEquals(){
		assertEquals(time, new Timestamp(1252243484L) );
	}
	
	@Test
	public void testTimestampIdentityIsEqual(){
		assertEquals(time, time);
	}
	
	
	@Test
	public void testTimestampAreEqualsWithOtherObject(){
		assertFalse(time.equals(new String("abcdef1234ghilmn")));
	}
	
	@Test
	public void testTimestampAreEqualsWithNullObject(){
		assertFalse(time.equals(null));
	}
	
	@Test
	public void testTimestampWithDifferentTimestamp(){
		assertFalse(time.equals(new Timestamp(2252243484L)));
	}

	
	@Test
	public void testWithAbsoluteTime() {
		time = new MockTimestamp(1267739498L);
		assertEquals(1267739498L, time.getExpire());
	}
	
	@Test
	public void testWithRelativeTime() {
		time = new MockTimestamp(190L);
		assertEquals(1252101288L, time.getExpire());
	}
	
	@Test
	public void testWithNullTime() {
		time = new MockTimestamp(0L);
		assertEquals(0L, time.getExpire());
	}
	
	@Test
	public void testWithNegativeTime() {
		time = new MockTimestamp(-20L);
		assertEquals(0L, time.getExpire());
	}
	
	@Test
	public void testWithThresholdTime() {
		time = new MockTimestamp(30*24*60*60);
		assertEquals(1254693098L, time.getExpire());
	}
	
	@Test
	public void testWithThresholdTimePlus1() {
		time = new MockTimestamp(30*24*60*60+1);
		assertEquals(2592001L, time.getExpire());
	}
	
	@Test
	public void testShouldBeExpired() {
		time = new MockTimestamp(190L);
		this.now = 1252101290L;
		
		assertTrue(time.isExpired());
	}
	
	@Test
	public void testShouldNotBeExpired() {
		time = new MockTimestamp(190L);
		this.now = 1252101286L;
		
		assertFalse(time.isExpired());		
	}
	
	@Test
	public void testShouldNeverExpire() {
		time = new MockTimestamp(0L);
		
		assertFalse(time.isExpired());
	}
	
	@Test
	public void testCompareIdentity() {
		time = new Timestamp(1267739498L);
		assertTrue(time.compareTo(time)==0);
	}
	
	@Test
	public void testCompareLess() {
		time = new Timestamp(1267739498L);
		Timestamp time2 = new Timestamp(1267739497L);
		assertTrue(time.compareTo(time2)>0);
	}
	
	@Test
	public void testCompareMore() {
		time = new Timestamp(1267739498L);
		Timestamp time2 = new Timestamp(1267739499L);
		assertTrue(time.compareTo(time2)<0);		
	}
	
	@Test
	public void testCompareEquals() {
		time = new Timestamp(1267739498L);
		Timestamp time2 = new Timestamp(1267739498L);
		assertTrue(time.compareTo(time2)==0);
	}
	
	@Test
	public void testHashcode() {
		assertEquals(1252244011, time.hashCode());
	}
	
	@Test
	public void testCompareShouldThrowNPE(){
		time = new Timestamp(1267739498L);
		try{
			time.compareTo(null);
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}
}
