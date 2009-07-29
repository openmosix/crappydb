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

package org.bonmassar.crappydb.server.storage.data;

public interface CASId {
	
	/**
	 * Compare this CASId with another CASId
	 * If the two CASIds are identicals, the item was not updated
	 * and the cas command succeds. If the two CASIds does not match,
	 * the item was updated and the transaction must be aborted.
	 * 
	 * @param otherCAS The second item involved in the transaction
	 * @return True if the two CASId match, false otherwise
	 * 
	 */
	boolean compareTo(String otherCASId);
	
	/**
	 * Convert this CASId into a casid value to be transmitted over the memcache protocol
	 * 
	 * @return A unique identifier conforming with memcache protocol restrictions
	 */
	String getValue();
	
}
