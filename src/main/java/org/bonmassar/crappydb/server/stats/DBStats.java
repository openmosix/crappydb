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
import java.util.HashMap;
import java.util.Map;

//!
public enum DBStats {
	INSTANCE;
	
	private final String version = "0.1";
	private final ProcessStats process = new ProcessStats();
	private final ServerTime serverTime = new ServerTime();
	private final StorageStats storage = new StorageStats();
	private final ConnectionsStats connections = new ConnectionsStats(); 
	private final ProtocolStats protocol = new ProtocolStats();
	
	public String getDBVersion() {
		return version;
	}
	
	public Map<String, String> getStats() {
		Map<String, String> stats = new HashMap<String, String>();
		collectServerVersion(stats);
		collectProcessStats(stats);
		collectServerTimeStats(stats);
		collectConnectionsStats(stats);
		collectProtocolStats(stats);
		collectStorageStats(stats);
		return stats;
	}
	
	public StorageStats getStorage() {
		return storage;
	}
	
	public ConnectionsStats getConnections() {
		return connections;
	}
	
	public ProtocolStats getProtocol(){
		return protocol;
	}
	
	private void collectServerVersion(Map<String, String> result) {
		result.put("version", getDBVersion());
	}

	private void collectProcessStats(Map<String, String> result) {
		result.put("pid", process.getPid());
		result.put("limit_maxbytes", process.getMaxBytesLimit());
		result.put("pointer_size", process.getPointerSize());
		result.put("threads", process.getThreads());
	}

	private void collectProtocolStats(Map<String, String> result) {
		result.put("cmd_get", protocol.getCumulativeGets());
		result.put("cmd_set", protocol.getCumulativeSets());
		result.put("evictions", protocol.getEvictions());
		result.put("get_hits", protocol.getNoHits());
		result.put("get_misses", protocol.getNoMisses());
	}

	private void collectServerTimeStats(Map<String, String> result) {
		result.put("rusage_system", serverTime.getSystemUsageTime());
		result.put("rusage_user", serverTime.getUserUsageTime());
		result.put("time", serverTime.getCurrentTime());
		result.put("uptime", serverTime.getUptime());
	}

	private void collectConnectionsStats(Map<String, String> result) {
		result.put("bytes_read", connections.getBytesRead());
		result.put("bytes_written", connections.getBytesWritten());
		result.put("connection_structures", connections.getConnectionStructures());
		result.put("curr_connections", connections.getCurrentNoConnections());
		result.put("total_connections", connections.getTotalNoConnections());
	}

	private void collectStorageStats(Map<String, String> result) {
		result.put("bytes", storage.getCurrentNoBytes());
		result.put("total_items", storage.getTotalNoItems());
		result.put("curr_items", storage.getCurrentNoItems());
	}

}
