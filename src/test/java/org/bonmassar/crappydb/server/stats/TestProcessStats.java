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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestProcessStats {

	private ProcessStats stats;
	
	@Before
	public void setUp() {
		stats = new ProcessStats();
	}
	
	@Test
	public void testNoThreads() {
		stats.newThread();
		stats.newThread();
		stats.newThread();
		stats.newThread();
		stats.newThread();
		assertEquals("5", stats.getThreads());
	}
	
	@Test
	public void testMaxBytesLimit() {
		assertEquals(String.valueOf(Runtime.getRuntime().maxMemory()), stats.getMaxBytesLimit());
	}
	
	@Test
	public void testPointerSize() {
		assertEquals(System.getProperty("sun.arch.data.model", "32"), stats.getPointerSize());
	}
	
	@Test
	public void testGetPid() {
		assertTrue(Long.valueOf(stats.getPid())>0);
	}
}
