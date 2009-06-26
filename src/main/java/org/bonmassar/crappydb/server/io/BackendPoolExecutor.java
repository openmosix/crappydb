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

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class BackendPoolExecutor {

	private final static int nBackendThread=5;
	
	private Logger logger = Logger.getLogger(BackendPoolExecutor.class);
	private LinkedBlockingQueue<ServerCommand> commandsQueue;
	private ExecutorService commandsExecutor;

	public BackendPoolExecutor() {
		initBackendThreads();
	}

	public void newCommand(ServerCommand cmd) {
		boolean result = commandsQueue.offer(cmd);
		if(result)
			logger.debug(String.format("Added new server command to queue (%s)", cmd));
		else
			logger.debug(String.format("Failed to add new server command to queue (%s)", cmd));
	}
	
	protected void initBackendThreads() {
		commandsQueue = new LinkedBlockingQueue<ServerCommand>();
		commandsExecutor = Executors.newFixedThreadPool(BackendPoolExecutor.nBackendThread);
		for(int i = 0; i < BackendPoolExecutor.nBackendThread; i++)
			commandsExecutor.submit (new FutureTask<Integer> (new BackendTask(commandsQueue)));
	}
	
}
