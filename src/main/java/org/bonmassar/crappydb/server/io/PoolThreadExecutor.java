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
import java.util.concurrent.LinkedBlockingQueue;
import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;

import org.apache.log4j.Logger;

public abstract class PoolThreadExecutor<T> {

	private int nThreads;
	protected LinkedBlockingQueue<T> queue;
	protected ExecutorService executor;
	protected DynamicCyclicBarrier syncPoint;

	private Logger logger = Logger.getLogger(PoolThreadExecutor.class);

	public PoolThreadExecutor() {
		nThreads = 1;
		syncPoint = new NullCyclicBarrier();
		initFrontendThreads();
	}
	
	public PoolThreadExecutor(int nThreads) {
		this.nThreads = nThreads;
		syncPoint = new NullCyclicBarrier();
		initFrontendThreads();
	}
	
	public void offer(T key) {
		boolean result = queue.offer(key);
		if(result)
			logger.debug(String.format("Added new item to the processing queue (%s)", key.toString()));
		else
			logger.debug(String.format("Failed to add new item (%s) to the processing queue", key.toString()));
	}
	
	public T take() throws InterruptedException {
		return queue.take();
	}
	
	public void enableSyncPoint() {
		syncPoint = new CyclicCountDownLatch();
	}
	
	public DynamicCyclicBarrier getBarrier() {
		return syncPoint;
	}
	
	protected void initFrontendThreads() {
		queue = new LinkedBlockingQueue<T>();
		executor = Executors.newFixedThreadPool(nThreads);
		Registry.INSTANCE.book(executor);
		for(int i = 0; i < nThreads; i++)
			executor.submit (new FutureTask<Integer> ( createNewTask() ));
	}
	
	protected abstract Callable<Integer> createNewTask();	
}
