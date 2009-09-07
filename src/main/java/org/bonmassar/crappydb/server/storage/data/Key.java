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

public class Key implements Comparable<Key> {
	
	/**
	 * Maximum size for an item key as defined by memcache protocol.
	 * See 'Keys' in http://code.sixapart.com/svn/memcached/trunk/server/doc/protocol.txt
	 */
	private final static int MAX_KEY_SIZE=250;

	private final String key;
	
	public Key(final String value){
		checkInvalidKey(value);
		key = truncateKeyIfLongerMaximumSize(value.trim()); 
	}
	
	public String toString() {
		return key;
	}

	@Override
	public boolean equals(Object dest) {
		if(!(dest instanceof Key))
			return false;
		
		if(this == dest)
			return true;

		return key.equals(((Key)dest).key);
	}

	@Override
	public int hashCode() {
		return 31 * 17 + key.hashCode();
	}
	
	public int compareTo(Key k) {
		if(null == k)
			throw new NullPointerException();
		
		return key.compareTo(k.key);
	}
	
	
	private void checkInvalidKey(String value) {
		if(null == value || 0 == value.trim().length())
			throw new IllegalArgumentException("Storage key cannot be null.");	
	}
	
	private String truncateKeyIfLongerMaximumSize(String value) {
		if(value.length() <= MAX_KEY_SIZE)
			return value;
		
		return value.substring(0, MAX_KEY_SIZE);
	}
}
