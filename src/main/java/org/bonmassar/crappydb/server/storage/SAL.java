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

import java.util.List;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

public abstract class SAL implements StorageAccessLayer, SALBuilder {
	
	protected final PhysicalAccessLayer delegate;
	
	public SAL(PhysicalAccessLayer delegate){
		this.delegate = delegate; 
	}
		
	public void add(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		delegate.add(item);
	}

	public Item append(Item item) throws NotFoundException, StorageException {
		checkItem(item);
		return delegate.append(item);
	}

	public Item prepend(Item item) throws NotFoundException, StorageException {
		checkItem(item);
		return delegate.prepend(item);
	}
	
	public Item replace(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		return delegate.replace(item);
	}

	public Item delete(Key id, Long time) throws NotFoundException, StorageException {
		checkValidId(id);
		return delegate.delete(id, time);
	}

	public List<Item> get(List<Key> ids) throws StorageException {
		checkValidIds(ids);
		return delegate.get(ids);
	}

	public Item set(Item item) throws StorageException {
		checkItem(item);
		return delegate.set(item);
	}

	public Item swap(Item item, String transactionid) throws NotFoundException, ExistsException,
			StorageException {
		checkItem(item);
		if(null == transactionid || transactionid.length() == 0)
			throw new StorageException("No CAS");
		
		return delegate.swap(item, transactionid);
	}

	public Item decrease(Key id, String value) throws NotFoundException,
			StorageException {
		checkValidId(id);
		return delegate.decrease(id, value);
	}

	public Item increase(Key id, String value) throws NotFoundException,
			StorageException {
		checkValidId(id);
		return delegate.increase(id, value);
	}
	
	public void flush(Long time) {
		delegate.flush(time);
	}
	
	public Item expire(Key k) {
		if(null == k)
			throw new NullPointerException("Key is null");

		return delegate.expire(k);
	}
	
	public void close() { 
		delegate.close();
	}

	private void checkItem(Item item) throws StorageException {
		if (null == item)
			throw new StorageException("Null item");
		if (item.isExpired())
			throw new StorageException("Item is expired");
	}

	private void checkValidIds(List<Key> ids) throws StorageException {
		if (null == ids || 0 == ids.size())
			throw new StorageException("No valid ids");
	}

	private void checkValidId(Key id) throws StorageException {
		if (null == id)
			throw new StorageException("No valid id");
	}
}
