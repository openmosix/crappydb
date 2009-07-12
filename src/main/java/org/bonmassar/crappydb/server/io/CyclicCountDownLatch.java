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

package org.bonmassar.crappydb.server.io;

import org.apache.log4j.Logger;

class CyclicCountDownLatch implements DynamicCyclicBarrier {

	private int counter;
	private int limit;
	
	private Logger logger = Logger.getLogger( CyclicCountDownLatch.class );  
	
	public CyclicCountDownLatch() {
		counter = limit = 0;
	}
	
	public synchronized void countDown() {
		if( limit == ++counter )
			notify();
	}
	
	public synchronized void await(int count) {
		if( shouldWait(count) )
			safeWait();
		
		counter = 0;
	}
	
	public synchronized void reset() {
		counter = -1;
		notify();
	}

	private boolean shouldWait(int count) {
		return counter >= 0 && count > 0 && ( limit = count ) != counter;
	}

	private void safeWait() {
		try {
			wait();
		} catch (InterruptedException e) {
			logger.fatal( "Cyclic CountDown latch was broken by interruption!", e );
		}
	}

}
