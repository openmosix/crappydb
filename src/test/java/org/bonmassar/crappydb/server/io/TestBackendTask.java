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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import junit.framework.TestCase;

import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestBackendTask extends TestCase {

	class IncreaseCounter implements Answer<Integer> {

		private StringBuilder counter;
		
		public IncreaseCounter(StringBuilder counter) {
			this.counter = counter;
		}
		
		public Integer answer(InvocationOnMock invocation) throws Throwable {
			synchronized (counter) {
				counter.append("a");
				Thread.sleep(50);
			}
			return null;
		}
		
	};
	
	private ExecutorService commandsExecutor;

	private LinkedBlockingQueue<ServerCommand> queue;
	
	@Before
	public void setUp(){
		queue = new LinkedBlockingQueue<ServerCommand>();
		commandsExecutor =  Executors.newFixedThreadPool(3);
		
		for(int i = 0; i < 3; i++)
			commandsExecutor.submit (new FutureTask<Integer> (new BackendTask(queue)));
	}
	
	@After
	public void tearDown(){
		commandsExecutor.shutdownNow();
	}
		
	@Test
	public void testExecutorDidItsJob(){
		StringBuilder counter = new StringBuilder();
		
		for (int i = 0; i < 100; i++){
			ServerCommand cmd = mock(ServerCommand.class);
			doAnswer(new IncreaseCounter(counter)).when(cmd).execCommand();
			queue.offer(cmd);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(100L, counter.toString().length());
	}
	
}
