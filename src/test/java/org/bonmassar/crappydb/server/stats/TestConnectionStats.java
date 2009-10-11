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

public class TestConnectionStats {

	private ConnectionsStats stats;
	
	@Before
	public void setUp(){
		stats = new ConnectionsStats();
	}
	
	@Test
	public void testNewSend() {
		stats.newSend(1500);
		stats.newSend(1200);
		stats.newSend(300);
		assertEquals("3000", stats.getBytesWritten());
	}
	
	@Test
	public void testNewReceive() {
		stats.newReceive(1500);
		stats.newReceive(1200);
		stats.newReceive(300);
		assertEquals("3000", stats.getBytesRead());
	}
	
	@Test
	public void testNumConnections() {
		stats.newConnection();
		stats.newConnection();
		stats.newConnection();
		stats.closeConnection();
		stats.newConnection();
		stats.newConnection();
		stats.newConnection();
		stats.closeConnection();
		stats.closeConnection();
		stats.newConnection();
		stats.newConnection();
		stats.newConnection();
		stats.closeConnection();
		assertEquals("9", stats.getTotalNoConnections());
		assertEquals("5", stats.getCurrentNoConnections());
		assertEquals("5", stats.getConnectionStructures());
	}
}
