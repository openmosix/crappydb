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

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

class ProcessStats {
	
	private AtomicInteger noThreads = new AtomicInteger();
	
	public void newThread(){
		noThreads.incrementAndGet();
	}
	
	/**
	 * @return process id of this server
	 */
	String getPid() {
		String bean = ManagementFactory.getRuntimeMXBean().getName();
		
		if(bean.contains("@"))
			bean = bean.substring(0, bean.indexOf("@"));
		
		return bean;
	}
	
	/**
	 * @return architecture pointer size (e.g.: 32, 64, etc.)
	 */
	String getPointerSize() {
		return System.getProperty("sun.arch.data.model", "32");
	}
	
	/**
	 * @return max memory limit (bytes) for this server
	 */
	String getMaxBytesLimit() {
		return String.valueOf(Runtime.getRuntime().maxMemory());
	}
	
	/**
	 * @return number of threads running on this server
	 */
	String getThreads() {
		return Integer.toString(noThreads.get());
	}
	
}
