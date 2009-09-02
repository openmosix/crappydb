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

import java.util.concurrent.atomic.AtomicLong;

public class ConnectionsStats {
	
	private AtomicLong written = new AtomicLong();
	private AtomicLong read = new AtomicLong();
	private AtomicLong noTotConnections = new AtomicLong();
	private AtomicLong noCurConnections = new AtomicLong();
	
	public void newSend(int delta){
		written.getAndAdd(delta);
	}
	
	public void newReceive(int delta){
		read.getAndAdd(delta);
	}

	public void newConnection() {
		noTotConnections.getAndIncrement();
		noCurConnections.getAndIncrement();
	}
	
	public void closeConnection() {
		noCurConnections.getAndDecrement();
	}
	
	String getCurrentNoConnections() {
		return Long.toString(noCurConnections.get());
	}
	
	String getTotalNoConnections() {
		return Long.toString(noTotConnections.get());
	}
	
	String getConnectionStructures() {
		return getCurrentNoConnections();
	}
	
	String getBytesRead() {
		return Long.toString(read.get());
	}
	
	String getBytesWritten() {
		return Long.toString(written.get());
	}
}
