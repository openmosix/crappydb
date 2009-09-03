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

import java.util.concurrent.atomic.AtomicLong;

public class StorageStats {
	
	private final AtomicLong totalItems = new AtomicLong();
	private final AtomicLong currItems = new AtomicLong();
	private final AtomicLong currBytes = new AtomicLong();
	
	String getCurrentNoItems() {
		return String.valueOf(currItems.get());
	}
	
	String getTotalNoItems() {
		return String.valueOf(totalItems.get());
	}
	
	String getCurrentNoBytes() {
		return String.valueOf(currBytes.get());
	}
	
	public void incrementNoItems() {
		totalItems.incrementAndGet();
		currItems.incrementAndGet();
	}
	
	public void decrementNoItems() {
		currItems.decrementAndGet();
	}
	
	public void addBytes(int noBytes) {
		currBytes.addAndGet(noBytes);
	}
	
	public void delBytes(int noBytes) {
		currBytes.addAndGet(-1 * noBytes);
	}

	
	public void reset() {
		currItems.set(0);
		currBytes.set(0);
	}

}
