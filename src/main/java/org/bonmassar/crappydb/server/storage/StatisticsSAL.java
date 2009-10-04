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
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

class StatisticsSAL extends GcSAL {

	public StatisticsSAL(PhysicalAccessLayer delegate) {
		super(delegate);
	}

	@Override
	public void add(Item item) throws NotStoredException, StorageException {
		super.add(item);
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		DBStats.INSTANCE.getStorage().incrementNoItems();
	}
	
	@Override
	public Item append(Item item) throws NotFoundException, StorageException {
		Item prevstored = super.append(item);
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		return prevstored;
	}

	@Override
	public Item prepend(Item item) throws NotFoundException, StorageException {
		Item prevstored = super.prepend(item);
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		return prevstored;
	}
	
	@Override
	public Item replace(Item item) throws NotStoredException, StorageException {
		Item old = super.replace(item);
		DBStats.INSTANCE.getStorage().delBytes(size(old.getData()));
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		return old;
	}
	
	@Override
	public Item delete(Key id, Long time) throws NotFoundException, StorageException {
		Item old = super.delete(id, time);
		DBStats.INSTANCE.getStorage().delBytes(size(old.getData()));
		DBStats.INSTANCE.getStorage().decrementNoItems();
		return old;
	}
	
	@Override
	public Item set(Item item) throws StorageException {
		Item old = super.set(item);
		
		if(null != old){
			DBStats.INSTANCE.getStorage().delBytes(size(old.getData()));
			DBStats.INSTANCE.getStorage().decrementNoItems();
		}

		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		DBStats.INSTANCE.getStorage().incrementNoItems();
		return old;
	}
	
	@Override
	public Item swap(Item item, String transactionid) throws NotFoundException, ExistsException, StorageException{
		Item old = super.swap(item, transactionid);
		DBStats.INSTANCE.getStorage().delBytes(size(old.getData()));
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		return old;
	}
	
	@Override
	public void flush(Long time) {
		super.flush(time);
		DBStats.INSTANCE.getStorage().reset();
	}
	
	@Override
	public Item expire(Key k) {
		Item old = super.expire(k);
		if(null == old)
			return null;
		
		DBStats.INSTANCE.getStorage().delBytes(size(old.getData()));
		DBStats.INSTANCE.getStorage().decrementNoItems();
		return old;
	}
	
	@Override
	public void notifyEviction(Item it) {
		super.notifyEviction(it);
		DBStats.INSTANCE.getStorage().delBytes(size(it.getData()));
		DBStats.INSTANCE.getStorage().decrementNoItems();
	}
		
	private int size(byte[] data) {
		if(null == data)
			return 0;
		return data.length;
	}

}
