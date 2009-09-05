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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bonmassar.crappydb.server.storage.Expirable;

public class FixedRateGarbageCollector {
	
	private final static long initialGCDelay = 60;	//1 Minute
	private final static long gcRate = 6*60;	//Every 6 minutes, 10 times/hour
	
	private static class GCTask implements Runnable {

		private final Cleaner garbageCollector;
		
		public GCTask(Cleaner garbageCollector) {
			this.garbageCollector = garbageCollector;  
		}
		
		public void run() {
			garbageCollector.expire();
			System.gc();
		}
		
	}
	
	private final ScheduledExecutorService scheduler;
	private final InternalGarbageCollector garbageCollector;
	
	public FixedRateGarbageCollector(Expirable container) {
		this(Executors.newScheduledThreadPool(1), new InternalGarbageCollector(container));
	}
	
	public FixedRateGarbageCollector(ScheduledExecutorService executorService, InternalGarbageCollector collector){
		this.scheduler = executorService;
		this.garbageCollector = collector;
	}
	
	public GarbageCollector getGCRef() {
		return garbageCollector;
	}
	
	public void startGC(){
		scheduler.scheduleAtFixedRate(new GCTask(garbageCollector), initialGCDelay, gcRate, TimeUnit.SECONDS);
	}
}
