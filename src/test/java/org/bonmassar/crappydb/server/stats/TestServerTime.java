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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestServerTime {

	private ServerTime time;
	
	class FakeWorker implements Runnable {

		private int doSomething = 0;
		
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				System.out.println(String.format("Yeah! Doing something %d", doSomething++));
			}
			System.out.println("Bye!");
		}
		
	}
	
	@Before
	public void setUp() {
		time = new ServerTime();
	}
	
	@Test
	public void testUptime() throws InterruptedException {
		Thread.sleep(1000);
		assertTrue(Long.parseLong(time.getUptime())>0);
	}
	
	@Test
	public void testCurrentTime() {
		long now = System.currentTimeMillis() / 1000;
		assertTrue(Long.parseLong(time.getCurrentTime()) - now < 1000);
	}
	
	@Test
	public void testSystemAndUserTime() throws InterruptedException {
		Thread tr = new Thread(new FakeWorker());
		tr.start();
		time.registerThreadId(tr.getId());
		Thread.sleep(5000);
		long system =  Long.parseLong(time.getSystemUsageTime().substring(2));
		long user = Long.parseLong(time.getUserUsageTime().substring(2));
		
		tr.interrupt();
		assertTrue(system > 0);
		assertTrue(user > 0);
	}
}
