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
	private int flags;
	private byte[] data;
	private long expire;
	
	public Item(Key storagekey, byte[] data, int flags){
		this.storagekey = storagekey;
		init(data, flags);
	}
	
	public void setExpire(long newexpire){
		this.expire = newexpire;
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
	
	private void init( byte[] data, int flags){
		if (null==storagekey)
			throw new NullPointerException();
		
		this.data = data;
		this.flags = flags;
	}
	
}
