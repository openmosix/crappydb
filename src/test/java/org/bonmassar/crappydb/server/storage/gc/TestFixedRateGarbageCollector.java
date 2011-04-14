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

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import junit.framework.TestCase;

public class TestFixedRateGarbageCollector extends TestCase {
	private FixedRateGarbageCollector scheduler;
	private ScheduledExecutorService service;
	private InternalGarbageCollector gc;
	
	@Before
	public void setUp() {
		service = Executors.newScheduledThreadPool(1);
		gc = mock(InternalGarbageCollector.class);
		scheduler = new FixedRateGarbageCollector(service,  gc, 10, 10);
	}
	
	@Test
	public void testIGotTheGC() {
		assertEquals(gc, scheduler.getGCRef());
	}
	
	@Test
	public void testIsSchedulingGC() {
		scheduler.startGC();
		pause(22);
		service.shutdown();
		verify(gc, times(2)).expire();
	}

	private void pause(int delay) {
		try {
			Thread.sleep(delay*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
