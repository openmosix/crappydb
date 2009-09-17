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

package org.bonmassar.crappydb.server.storage.gc;

import java.util.Set;
import java.util.TreeSet;

import org.bonmassar.crappydb.server.storage.Expirable;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.data.Timestamp;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.anyObject;

import junit.framework.TestCase;

public class TestInternalGarbageCollector extends TestCase {
	private final static long baseTimestamp = 1883395483L;
	private final static long expiredTimestamp = 1252238329L;
	private final static String baseKey = "terminenzio";

	private InternalGarbageCollector collector;
	private Expirable container;
	private Set<ReferenceBean> treemap;
	
	class ExpiredTimestamp extends Timestamp {

		public ExpiredTimestamp(long timestamp) {
			super(timestamp);
		}
		
		@Override
		public long now() {
			return 1252248229L;
		}
	}
	
	@Before
	public void setUp() {
		container = mock(Expirable.class);
		treemap = new TreeSet<ReferenceBean>();
		collector = new InternalGarbageCollector(container, new ChangesCollector(), treemap);
	}
	
	@Test
	public void testGarbageCollectionRainbow( ){
		generateIncoming();
		generatePrevTimeMap();
		collector.expire();
		checkExpired();
		checkTimeMapWithIncoming();
	}

	@Test
	public void testGarbageCollectionNoIncoming(){
		generatePrevTimeMap();
		collector.expire();
		checkExpired();		
		checkTimeMapWithoutIncoming();
	}
	
	@Test
	public void testGarbageCollectionEmptyMap(){
		generateIncoming();
		collector.expire();
		verify(container, times(0)).expire((Key) anyObject());
		checkTimeMapOnlyIncoming();
	}
	
	private void checkTimeMapOnlyIncoming() {
		for(int i = 0; i < 300; i++){
			assertTrue(treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 4000; i < 4080; i++){
			assertTrue("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+1000*i))));			
		}
	}

	private void checkTimeMapWithIncoming() {
		for(int i = 0; i < 300; i++){
			assertTrue(treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 3000; i < 4000; i++){
			assertTrue("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 4000; i < 4080; i++){
			assertTrue("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+1000*i))));			
			assertFalse("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 4080; i < 5000; i++){
			assertTrue("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 5000; i < 5050; i++){
			assertFalse("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 5050; i < 6000; i++){
			assertTrue("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 6000; i < 7000; i++){
			assertFalse("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
	}
	
	private void checkTimeMapWithoutIncoming() {
		for(int i = 3000; i < 6000; i++){
			assertTrue("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 6000; i < 7000; i++){
			assertFalse("iteration"+i, treemap.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
	}

	
	private void generatePrevTimeMap() {
		for(int i = 3000; i < 6000; i++){
			treemap.add(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i)));
		}
		for(int i = 6000; i < 7000; i++){
			treemap.add(new ReferenceBean(new Key(baseKey+i), new ExpiredTimestamp(expiredTimestamp+i)));
		}
	}

	private void generateIncoming() {
		for(int i = 0; i < 300; i++){
			collector.monitor(new Key(baseKey+i), baseTimestamp+i);
		}
		for(int i = 4000; i < 4080; i++){
			collector.replace(new Key(baseKey+i), baseTimestamp+i, baseTimestamp+1000*i);
		}
		for(int i = 5000; i < 5050; i++){
			collector.stop(new Key(baseKey+i), baseTimestamp+i);
		}
	}
	
	private void checkExpired() {
		for(int i = 6000; i < 7000; i++){
			verify(container, times(1)).expire(new Key(baseKey+i));
		}
	}
	
}
