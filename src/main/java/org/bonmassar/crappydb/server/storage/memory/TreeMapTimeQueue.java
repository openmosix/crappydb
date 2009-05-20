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
package org.bonmassar.crappydb.server.storage.memory;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


public class TreeMapTimeQueue implements TimeQueue {
	
	private TreeMap<Long, List<Key>> queue;
	
	public TreeMapTimeQueue() {
		queue = new TreeMap<Long, List<Key>>();
	}

	public void add(Item elem) {
		if(null == elem || null == elem.getExpire())
			return;
		
		add(elem.getExpire(), elem.getKey());
	}

	public List<Key> expireNow() {
		Long now = getNow();
		List<Key> expired = new LinkedList<Key>();
		synchronized(queue){
			Long cursor = 0L;
			while(null != (cursor = getFirstTimer())){
				if(cursor < now)
					break;
				
				expired.addAll(queue.remove(cursor));
			}
		}
		return expired;
	}

	public void stop(Item ie) {
		if(null == ie || null == ie.getExpire())
			return;
		
		stop(ie.getExpire(), ie.getKey());
	}
	
	protected Long getNow() {
		return System.currentTimeMillis() / 1000;
	}
	
	private Long getFirstTimer() {
		return queue.firstKey();
	}

	private void add(Long expire, Key k){
		synchronized(queue){
			List<Key> keys = getTimerOrCreateIt(expire);

			keys.add(k);
			queue.put(expire, keys);
		}
	}
	
	private void stop(Long expire, Key key) {
		synchronized(queue){
			List<Key> keys = queue.get(expire);
			if(null == keys)
				return;
			if(isLastElement(keys)){
				queue.remove(expire);
				return;
			}
			keys.remove(key);			
		}
	}

	private boolean isLastElement(List<Key> keys) {
		return 1 == keys.size();
	}
	
	private List<Key> getTimerOrCreateIt(Long expire){
		List<Key> keys = queue.get(expire);
		if(null == keys)
			keys = new LinkedList<Key>();
		
		return keys;
	}
}