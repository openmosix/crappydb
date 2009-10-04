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

package org.bonmassar.crappydb.server.storage;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.data.Timestamp;
import org.bonmassar.crappydb.server.storage.gc.GarbageCollectorScheduler;
import org.bonmassar.crappydb.server.storage.gc.NullGarbageCollectorScheduler;

class GcSAL extends SAL {

	private GarbageCollectorScheduler gc = new NullGarbageCollectorScheduler(null);
	
	public GcSAL(PhysicalAccessLayer delegate) {
		super(delegate);
	}
	
	public void setGarbageCollector(GarbageCollectorScheduler gc){
		if(null == gc)
			throw new NullPointerException();
		
		this.gc = gc;
	}

	@Override
	public void add(Item item) throws NotStoredException, StorageException {
		super.add(item);
		gc.getGCRef().monitor(item.getKey(), item.getExpire());
	}
	
	@Override
	public Item append(Item item) throws NotFoundException, StorageException {
		Item prevstored = super.append(item);

		if(null != prevstored)
			gc.getGCRef().replace(item.getKey(), item.getExpire(), prevstored.getExpire());

		return prevstored;
	}
	
	@Override
	public Item prepend(Item item) throws NotFoundException, StorageException {
		Item prevstored = super.prepend(item);

		if(null != prevstored)
			gc.getGCRef().replace(item.getKey(), item.getExpire(), prevstored.getExpire());

		return prevstored;
	}
	
	@Override
	public Item replace(Item item) throws NotStoredException, StorageException {
		Item prevstored = super.replace(item);
		gc.getGCRef().replace(item.getKey(), item.getExpire(), prevstored.getExpire());
		return prevstored;
	}
	
	@Override
	public Item delete(Key id, Long time) throws NotFoundException, StorageException {
		Item prevstored = super.delete(id, time);
		if(null == time || -1L == time)
			gc.getGCRef().stop(id, prevstored.getExpire());
		else 
			gc.getGCRef().replace(id, Timestamp.getMinTimestamp(time, prevstored.getExpire()).getExpire(),
					prevstored.getExpire());

		return prevstored;
	}
	
	@Override
	public Item set(Item item) throws StorageException {
		Item prevstored = super.set(item);
		gc.getGCRef().monitor(item.getKey(), item.getExpire());
		return prevstored;
	}
	
	@Override
	public Item swap(Item item, String transactionid) throws NotFoundException, ExistsException, StorageException {
		Item prevstored = super.swap(item, transactionid);
		gc.getGCRef().replace(item.getKey(), item.getExpire(), prevstored.getExpire());				
		return prevstored;
	}
	
	@Override
	public void flush(Long time) {
		super.flush(time);
		gc.getGCRef().flush();
	}
	
	public Item remove(Item it){
		gc.getGCRef().stop(it.getKey(), it.getExpire());
		return it;
	}

	public void close() { /* nothing to do */ }
}
