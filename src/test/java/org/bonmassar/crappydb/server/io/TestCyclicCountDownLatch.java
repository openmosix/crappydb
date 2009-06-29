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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestCyclicCountDownLatch extends TestCase {

	private CyclicCountDownLatch latch;
	protected ExecutorService executor;
	
	class TestCyclicCountDownLatchTask implements Callable<Integer> {
		
		private CyclicCountDownLatch intlatch;

		public TestCyclicCountDownLatchTask(CyclicCountDownLatch intlatch) {
			this.intlatch = intlatch;
		}
		
		public Integer call() throws Exception {
			for (int i = 0; i < 3; i++)
				intlatch.countDown();
			
			return null;
		}
	}
		
	@Before
	public void setUp(){
		latch = new CyclicCountDownLatch();
		executor = Executors.newFixedThreadPool(20);
	}
	
	@After
	public void tearDown(){
		executor.shutdownNow();
	}
	
	@Test
	public void testShouldBeWokenWhenAllTasksCompleted() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startThreads();
			}
		}.run();

		latch.await(60);
	}
	
	@Test
	public void testShouldNeverSleep() {
		startThreads();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		latch.await(60);
	}
	
	@Test
	public void testShouldRestartOnReset() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				latch.reset();
			}
		}.run();

		latch.await(60);
	}
	
	private void startThreads() {
		for(int i = 0; i < 20; i++)
			executor.submit (new FutureTask<Integer> ( new TestCyclicCountDownLatchTask(latch) ));
	}
	
}
