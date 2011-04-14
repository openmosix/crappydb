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
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;

public class ServerTime {
	
	private static Collection<Long> ids = new ArrayList<Long>();
	
	public void registerThreadId(long l){
		ids.add(l);
	}
	
	/**
	 * @return jvm uptime - number of seconds
	 */
	String getUptime() {
		return Long.toString(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
	}
	
	/**
	 * @return current epoch time - number of seconds
	 */
	String getCurrentTime() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * @return rusage of all threads - number of seconds:number of microseconds
	 */
	String getUserUsageTime() {
		return getUserTime();
	}

	/**
	 * @return sysusage of all threads - number of seconds:number of microseconds
	 */
	String getSystemUsageTime() {
		return getSystemTime();
	}
	
	private String getUserTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    if ( ! bean.isThreadCpuTimeSupported( ) )
	        return "NA";
	    long time = 0L;
	    for ( long i : ids ) {
	        long t = bean.getThreadUserTime( i )/1000;
	        if ( t != -1 )
	            time += t;
	    }
	    return Long.toString(time/(1000*1000))+":"+Long.toString(time);
	}
	  
	private String getSystemTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    if ( ! bean.isThreadCpuTimeSupported( ) )
	        return "NA";
	    long time = 0L;
	    for ( long i : ids ) {
	        long tc = bean.getThreadCpuTime( i )/1000;
	        long tu = bean.getThreadUserTime( i )/1000;
	        if ( tc != -1 && tu != -1 )
	            time += (tc - tu);
	    }
	    return Long.toString(time/(1000*1000))+":"+Long.toString(time);
	}
	
}
