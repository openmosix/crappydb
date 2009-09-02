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

public class ProtocolStats {
	
	private AtomicLong hit = new AtomicLong();
	private AtomicLong miss = new AtomicLong();
	private AtomicLong sets = new AtomicLong();
	
	public void newHit(){
		hit.incrementAndGet();
	}

	public void newMisses(long noMissed){
		miss.addAndGet(noMissed);
	}
	
	public void newSet() {
		sets.incrementAndGet();
	}

	/**
	 * @return number of cumulative get* requests
	 */
	String getCumulativeGets() {
		return Long.toString(hit.get()+miss.get());
	}

	/**
	 * @return number of cumulative set* requests
	 */
	String getCumulativeSets() {
		return Long.toString(sets.get());
	}
	
	/**
	 * @return number of hit in the cache
	 */
	String getNoHits() {
		return Long.toString(hit.get());
	}
	
	/**
	 * @return number of miss in the cache
	 */
	String getNoMisses() {
		return Long.toString(miss.get());
	}
	
	/**
	 * @return number of items removed from the cache due to memory restrictions
	 */
	String getEvictions() {
		return "0";
	}
}
