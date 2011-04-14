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

public class Item {
	
	private final Key storagekey;
	private final int flags;
	private final byte[] data;
	protected final Timestamp expire;
	
	public Item(final Key storagekey, final byte[] data, int flags){
		this(storagekey, data, flags, -1L);
	}
	
	public Item(final Key storagekey, final byte[] data, int flags, long expire){
		this.storagekey = storagekey;
		if (null==storagekey)
			throw new NullPointerException();
		
		this.data = data;
		this.flags = flags;
		this.expire = getTimestamp(expire);
	}
		
	public long getExpire(){
		if(null == expire)
			return 0L;
		
		return expire.getExpire();
	}
	
	public Key getKey() {
		return storagekey;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public CASId generateCAS() {
		return new CASImpl(flags, getExpire(), data);
	}
	
	public boolean isExpired() {
		if(null == expire)
			return false;
		
		return expire.isExpired();
	}
		
	protected Timestamp getTimestamp(long expire){
		if(expire <= 0)
			return Timestamp.NO_EXPIRE;
		
		return new Timestamp(expire);
	}
}
