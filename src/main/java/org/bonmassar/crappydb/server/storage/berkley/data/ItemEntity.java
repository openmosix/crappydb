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

package org.bonmassar.crappydb.server.storage.berkley.data;

import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class ItemEntity {

	@PrimaryKey
	private String primaryKey;
	
	private byte[] payload;
	private int flags;
	private long expiration;

	ItemEntity(){
		//injection constructor
	}
	
	public ItemEntity(final Item item){
		if(null == item)
			throw new NullPointerException();
		
		primaryKey = item.getKey().toString();
		payload = item.getData();
		flags = item.getFlags();
		expiration = item.getExpire();
	}
	
	public Item toItem() {
		return new Item(new Key(primaryKey), payload, flags, expiration);
	}
}
