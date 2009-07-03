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

import java.util.List;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

/**
 * A queue of pending keys that need to be expired
 */interface TimeQueue {

	/**
	 * Add an elem to the queue
	 * @param elem The elem to be added
	 */
	TimeQueue add(Item elem);
	
	/**
	 * Remove an element from the queue
	 * The element will be stopped and the expireNow() will not find it
	 * @param elem The element to be removed from the queue 
	 */
	TimeQueue stop(Item elem);
	
	/**
	 * Check all keys that must be expired now.
	 * This method can be polled regularly and all keys expired are returned
	 * @return List of the keys that are expired
	 */
	List<Key> expireNow();
	
}
