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

package org.bonmassar.crappydb.server;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

public class ShutdownExecutionRegister extends Thread {
	
	public enum Registry {
		INSTANCE;
		
		private Queue<ExecutorService> registry;
		private Registry(){
			registry = new java.util.concurrent.ConcurrentLinkedQueue<ExecutorService>();
		}
		
		public boolean book(ExecutorService service){
			if(null == service)
				return false;
			
			return registry.add(service);
		}
		
		public int size(){
			return registry.size();
		}
		
		public void clear(){
			registry.clear();
		}
		
		public void shutdown(){
			for(ExecutorService es : registry)
				es.shutdownNow();
			
			clear();
		}
	}
	
	private Logger logger = Logger.getLogger(ShutdownExecutionRegister.class);
	
	public void run() {
		 logger.info("Shutdown of all executors...");
		 Registry.INSTANCE.shutdown();
		 logger.info("All executors terminated");
	}
}