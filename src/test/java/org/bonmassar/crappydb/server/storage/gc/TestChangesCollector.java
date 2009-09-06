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

import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.data.Timestamp;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import junit.framework.TestCase;

public class TestChangesCollector extends TestCase {
	private final static long baseTimestamp = 1267739498L;
	private final static String baseKey = "terminenzio";

	private ChangesCollector collector;
	
	@Before
	public void setUp() {
		collector = new ChangesCollector();
	}
	
	@Test
	public void testIncoming(){
		generateIncoming();
		for(int i = 0; i < 30; i++){
			assertTrue(collector.incoming.contains(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
		for(int i = 30; i < 40; i++){
			assertTrue(collector.incoming.contains(new ReplaceReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i), new Timestamp(baseTimestamp+1000*i))));
		}
		for(int i = 40; i < 50; i++){
			assertTrue(collector.incoming.contains(new DeleteReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i))));
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testVisit(){
		generateIncoming();
		Set<ReferenceBean> treemap = mock(Set.class);
		collector.visitIncoming(treemap);
		for(int i = 0; i < 30; i++){
			verify(treemap, times(1)).add(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i)));
		}
		for(int i = 30; i < 40; i++){
			verify(treemap, times(1)).remove(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+1000*i)));
			verify(treemap, times(1)).add(new ReplaceReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i), new Timestamp(baseTimestamp+1000*i)));
		}
		for(int i = 40; i < 50; i++){
			verify(treemap, times(1)).remove(new ReferenceBean(new Key(baseKey+i), new Timestamp(baseTimestamp+i)));
		}
	}
	
	private void generateIncoming() {
		for(int i = 0; i < 30; i++){
			collector.monitor(new Key(baseKey+i), new Timestamp(baseTimestamp+i));
		}
		for(int i = 30; i < 40; i++){
			collector.replace(new Key(baseKey+i), new Timestamp(baseTimestamp+i), new Timestamp(baseTimestamp+1000*i));
		}
		for(int i = 40; i < 50; i++){
			collector.stop(new Key(baseKey+i), new Timestamp(baseTimestamp+i));
		}
	}
}
