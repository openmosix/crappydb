/*  This file is part of CrappyDB-Server, 
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

import java.util.Arrays;

class CASImpl implements CASId {
	
	private long flags;
	private long expire;
	private byte[] payload;

	public CASImpl(long flags, long expire, byte[] payload ){		
		this.flags = flags;
		this.expire = expire;
		this.payload = payload;
	}
		
	public boolean compareTo(String otherCAS) {
		if(null == otherCAS)
			return false;

		return getValue().equalsIgnoreCase(otherCAS);
	}

	public String getValue() {
		long hash = 17;
		//order of chance of what can change => amplify difference
		hash = 31 * hash + Arrays.hashCode(payload);
		hash = 31 * hash + flags;
		hash = 31 * hash + expire;
		
		return Long.toString(hash < 0 ? -1*hash : hash);
	}

}
