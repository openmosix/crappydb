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

import java.io.Serializable;

public class Item {

	private Key	storagekey;
	private Integer flags;
	private Serializable data;
	private Cas internalcas;
	private Long expire;
	
	public Item(Key storagekey, Serializable data){
		init(storagekey, data, null);
	}
	
	public Item(Key storagekey, Serializable data, Integer flags){
		init(storagekey, data, flags);
	}
	
	public Item(Serializable data){
		init(null, data, null);
	}
	
	public Item(Serializable data, Integer flags){
		init( null, data, flags );
	}
	
	public void setCas(Cas newcas){
		this.internalcas = newcas;
	}
	
	public void setExpire(Long newexpire){
		this.expire = newexpire;
	}
	
	public Long getExpire(){
		return this.expire;
	}
	
	private void init(Key storagekey, Serializable data, Integer flags){
		this.data = data;
		this.flags = flags;
		this.storagekey = storagekey;
	}
	
	
}
