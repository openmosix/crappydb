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

package org.bonmassar.crappydb.server.stats;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestProtocolStats {

	private ProtocolStats stats;
	
	@Before
	public void setUp() {
		stats = new ProtocolStats();
	}
	
	@Test
	public void testEvictions() {
		assertEquals("0", stats.getEvictions());
	}
	
	@Test
	public void testGets() {
		stats.newHit();
		stats.newHit();
		stats.newHit();
		stats.newHit();
		stats.newMisses(5);
		stats.newMisses(3);
		assertEquals(4L, Long.parseLong(stats.getNoHits()));
		assertEquals(8L, Long.parseLong(stats.getNoMisses()));
		assertEquals(12L, Long.parseLong(stats.getCumulativeGets()));
	}
	
	@Test
	public void testSets() {
		stats.newSet();
		stats.newSet();
		stats.newSet();
		stats.newSet();
		stats.newSet();
		assertEquals(5L, Long.parseLong(stats.getCumulativeSets()));
	}
	
}
