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

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;

public class FrontendPoolExecutor {
	private final static int nFrontendThread=1;
	private LinkedBlockingQueue<SelectionKey> operationsQueue;
	private ExecutorService commandsExecutor;

	private Logger logger = Logger.getLogger(FrontendPoolExecutor.class);
	
	public FrontendPoolExecutor(CommandFactory cmdFactory, Selector serverSelectorForAccept, BackendPoolExecutor backend) {
		initFrontendThreads(cmdFactory, serverSelectorForAccept, backend);
	}
	
	protected void processRequest(SelectionKey key) {
		boolean result = operationsQueue.offer(key);
		if(result)
			logger.debug(String.format("Added new server operation to queue"));
		else
			logger.debug(String.format("Failed to add new operation command to queue"));

	}
	
	protected void initFrontendThreads(CommandFactory cmdFactory, Selector serverSelectorForAccept, BackendPoolExecutor backend) {
		operationsQueue = new LinkedBlockingQueue<SelectionKey>();
		commandsExecutor = Executors.newFixedThreadPool(FrontendPoolExecutor.nFrontendThread);
		for(int i = 0; i < FrontendPoolExecutor.nFrontendThread; i++)
			commandsExecutor.submit (new FutureTask<Integer> (new FrontendTask(cmdFactory,
					serverSelectorForAccept, backend, operationsQueue)));
	}


}
