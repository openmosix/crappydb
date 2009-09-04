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

/* From protocol spec: 
 * Some commands involve a client sending some kind of expiration time
 * (relative to an item or to an operation requested by the client) to
 * the server. In all such cases, the actual value sent may either be
 * Unix time (number of seconds since January 1, 1970, as a 32-bit
 * value), or a number of seconds starting from current time. In the
 * latter case, this number of seconds may not exceed 60*60*24*30 (number
 * of seconds in 30 days); if the number sent by a client is larger than
 * that, the server will consider it to be real Unix time value rather
 * than an offset from current time.*/

public class Item {

	//! any time less than this is considered relative, see above
	private final static long relativeTimeThreshold = 60*60*24*30;
	
	private final Key storagekey;
	private int flags;
	private byte[] data;
	private long expire;
	
	public Item(Key storagekey, byte[] data, int flags){
		this.storagekey = storagekey;
		init(data, flags);
	}
	
	public void setExpire(long newexpire){
		this.expire = getAbsoluteTime(newexpire);
	}
	
	public long getExpire(){
		return this.expire;
	}
	
	public Key getKey() {
		return storagekey;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] newdata){
		data = newdata;
	}
	
	public void setFlags(int flags){
		this.flags = flags;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public CASId generateCAS() {
		return new CASImpl(flags, expire, data);
	}
	
	private long getAbsoluteTime(long expire){
		if(expire <= 0)
			return 0;
		
		if(expire <= Item.relativeTimeThreshold)
			return now() + expire;
		
		return expire;
	}
	
	private void init( byte[] data, int flags){
		if (null==storagekey)
			throw new NullPointerException();
		
		this.data = data;
		this.flags = flags;
	}

	public long now() {
		return System.currentTimeMillis() / 1000;
	}
	
}
