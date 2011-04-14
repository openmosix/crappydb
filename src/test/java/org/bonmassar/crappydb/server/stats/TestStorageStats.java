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

public class TestStorageStats {

	private StorageStats stats;
	
	@Before
	public void setUp() {
		stats = new StorageStats();
	}
	
	@Test
	public void testBytes() {
		stats.addBytes(5000);
		stats.addBytes(5000);
		stats.delBytes(10000);
		stats.addBytes(12000);
		stats.delBytes(15000);
		stats.addBytes(3000);
		stats.addBytes(5000);
		stats.delBytes(1000);
		assertEquals("4000", stats.getCurrentNoBytes());
		stats.reset();
		assertEquals("0", stats.getCurrentNoBytes());
	}
	
	@Test
	public void testNumItems() {
		stats.incrementNoItems();
		stats.incrementNoItems();
		stats.incrementNoItems();
		stats.incrementNoItems();
		stats.decrementNoItems();
		stats.decrementNoItems();
		stats.decrementNoItems();
		stats.decrementNoItems();
		stats.decrementNoItems();
		stats.incrementNoItems();
		stats.incrementNoItems();
		stats.incrementNoItems();
		stats.incrementNoItems();
		assertEquals("3", stats.getCurrentNoItems());
		assertEquals("8", stats.getTotalNoItems());
		stats.reset();
		assertEquals("0", stats.getCurrentNoItems());
		assertEquals("8", stats.getTotalNoItems());
	}
}
