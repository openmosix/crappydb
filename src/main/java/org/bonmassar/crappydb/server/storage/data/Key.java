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

public class Key {
	
	/**
	 * Maximum size for an item key as defined by memcache protocol.
	 * See 'Keys' in http://code.sixapart.com/svn/memcached/trunk/server/doc/protocol.txt
	 */
	private final static int MAX_KEY_SIZE=250;

	private String value;
	
	public Key(String value){
		importKeyValue(value);
	}
	
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object dest) {
		if(null == dest || !(dest instanceof Key))
			return false;

		return value.equals(((Key)dest).value);
	}

	@Override
	public int hashCode() {
		if(null == value)
			return super.hashCode();
		
		return value.hashCode();
	}
	
	private void importKeyValue(String value) {
		this.value = value;
		checkInvalidKey();
		cleanupKey();
		truncateKeyIfLongerMaximumSize(); 
	}

	private void cleanupKey() {
		value = value.trim();
	}

	private void checkInvalidKey() {
		if(null == value || 0 == value.trim().length())
			throw new IllegalArgumentException("Storage key cannot be null.");	
	}
	
	private void truncateKeyIfLongerMaximumSize() {
		if(value.length() <= MAX_KEY_SIZE)
			return;
		
		value = value.substring(0, MAX_KEY_SIZE);
	}
}
