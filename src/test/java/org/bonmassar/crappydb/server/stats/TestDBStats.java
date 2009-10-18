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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class TestDBStats {
	
	@Test
	public void testMonoInstance(){
		assertEquals(1, DBStats.values().length);
	}
	
	@Test
	public void testValuesOf(){
		assertNotNull(DBStats.valueOf("INSTANCE"));
	}
	
	@Test
	public void testDBVersion(){
		assertEquals("0.3", DBStats.INSTANCE.getDBVersion());
	}
	
	@Test
	public void testGetStorage(){
		assertNotNull(DBStats.INSTANCE.getStorage());
	}
	
	@Test
	public void testGetProtocol(){
		assertNotNull(DBStats.INSTANCE.getProtocol());
	}
	
	@Test
	public void testGetConnections(){
		assertNotNull(DBStats.INSTANCE.getConnections());
	}
	
	@Test
	public void testStats() {
		Map<String, String> stats = DBStats.INSTANCE.getStats();
		assertNotNull(stats);
		assertEquals("0.3", stats.get("version"));
		assertTrue(Long.parseLong(stats.get("pid"))>0);
		assertTrue(Long.parseLong(stats.get("limit_maxbytes"))>0);
		assertTrue(Long.parseLong(stats.get("pointer_size"))>=32);
		assertTrue(Long.parseLong(stats.get("threads"))>=0);
		assertTrue(Long.parseLong(stats.get("cmd_get"))>=0);
		assertTrue(Long.parseLong(stats.get("cmd_set"))>=0);
		assertTrue(Long.parseLong(stats.get("evictions"))>=0);
		assertTrue(Long.parseLong(stats.get("get_hits"))>=0);
		assertTrue(Long.parseLong(stats.get("get_misses"))>=0);
		assertTrue(Long.parseLong(stats.get("rusage_system").substring(2))>=0);
		assertTrue(Long.parseLong(stats.get("rusage_user").substring(2))>=0);
		assertTrue(Long.parseLong(stats.get("time"))>=0);
		assertTrue(Long.parseLong(stats.get("uptime"))>=0);
		assertTrue(Long.parseLong(stats.get("bytes_read"))>=0);
		assertTrue(Long.parseLong(stats.get("bytes_written"))>=0);
		assertNotNull(Long.parseLong(stats.get("connection_structures")));
		assertTrue(Long.parseLong(stats.get("curr_connections"))>=0);
		assertTrue(Long.parseLong(stats.get("total_connections"))>=0);
		assertTrue(Long.parseLong(stats.get("bytes"))>=0);
		assertTrue(Long.parseLong(stats.get("total_items"))>=0);
		assertTrue(Long.parseLong(stats.get("curr_items"))>=0);
	}

}
