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

package org.bonmassar.crappydb.server.storage.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

public class UnboundedMap implements StorageAccessLayer {

	protected Map<Key, Item> repository;
	
	public UnboundedMap () {
		repository = new HashMap<Key, Item>();
	}
	
	public void add(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		blowIfItemExists(item);
		repository.put(item.getKey(), item);
	}

	public void append(Item item) throws StorageException {
		checkItem(item);
		blowIfItemDoesNotExists(item);
		if(noInternalData(item))
			return;
		
		Item prevstored = repository.get(item.getKey());
		prevstored.setData(concatData(item, prevstored));
	}

	public void delete(Key id) throws NotFoundException, StorageException {
		checkValidId(id);
		blowIfItemDoesNotExists(id);
		repository.remove(id);
	}

	public List<Item> get(List<Key> ids) throws NotFoundException,
			StorageException {
		checkValidIds(ids);
		List<Item> resp = new LinkedList<Item>();
		for (Key k : ids){
			if(null == k)
				continue;
			
			Item elem = repository.get(k);
			if(null != elem)
				resp.add(elem);
		}
		
		if(0 == resp.size())
			throw new NotFoundException("No data found");
		
		return resp;
	}

	public void set(Item item) throws StorageException {
		checkItem(item);
		repository.put(item.getKey(), item);
	}
	
	
	
	public Item decrease(Key id, Long value) throws NotFoundException,
	StorageException {
		throw new StorageException("Not Implemented");
	}
	
	public void swap(Item item) throws NotFoundException, ExistsException,
			StorageException {
		throw new StorageException("Not Implemented");
	}
	
	public Item increase(Key id, Long value) throws NotFoundException,
	StorageException {
		throw new StorageException("Not Implemented");
	}

	public void prepend(Item item) throws StorageException {
		throw new StorageException("Not Implemented");
	}

	public void replace(Item item) throws NotStoredException, StorageException {
		throw new StorageException("Not Implemented");
	}
	
	private void checkItem(Item item) throws StorageException {
		if(null == item)
			throw new StorageException("Null item");
		if(null == item.getKey())
			throw new StorageException("Invalid key");
	}
	
	private void blowIfItemExists(Item item) throws NotStoredException {
		if(repository.containsKey(item.getKey()))
			throw new NotStoredException("Data already exists for this key");
	}
	
	private void blowIfItemDoesNotExists(Item item) throws StorageException {
		blowIfItemDoesNotExists(item.getKey());
	}
	
	private void blowIfItemDoesNotExists(Key k) throws StorageException {
		if(!repository.containsKey(k))
			throw new StorageException("Unknown key");
	}
	
	private boolean noInternalData(Item item) {
		return noBinaryData(item.getData());
	}
	
	private boolean noBinaryData(byte[] data){
		return null == data || 0 == data.length;
	}

	private byte[] concatData(Item item, Item prevstored) {
		byte[] concatdata = new byte[computeNewInternalDataLength(prevstored.getData(), item.getData())];
		
		int cursor = 0;
		if(!noInternalData(prevstored)){
			cursor = prevstored.getData().length;
			System.arraycopy(prevstored.getData(), 0, concatdata, 0, prevstored.getData().length);
		}
		
		System.arraycopy(item.getData(), 0, concatdata, cursor, item.getData().length);
		return concatdata;
	}

	
	private int computeNewInternalDataLength(byte[] olddata, byte[] newdata){
		if(!noBinaryData(olddata))
			return olddata.length + newdata.length;
		
		return newdata.length;
	}

	private void checkValidIds(List<Key> ids) throws StorageException {
		if(null == ids || 0 == ids.size())
			throw new StorageException("No valid ids");
	}
	
	private void checkValidId(Key id) throws StorageException {
		if(null == id)
			throw new StorageException("No valid id");
	}


}
