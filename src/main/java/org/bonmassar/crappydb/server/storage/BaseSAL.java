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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.gc.GarbageCollectorScheduler;
import org.bonmassar.crappydb.server.storage.gc.NullGarbageCollectorScheduler;
import org.bonmassar.crappydb.utils.BigDecrementer;
import org.bonmassar.crappydb.utils.BigIncrementer;

public class BaseSAL implements StorageAccessLayer, SALBuilder {
	
	protected final Map<Key, Item> repository;
	private GarbageCollectorScheduler gc = new NullGarbageCollectorScheduler(null);
	
	public BaseSAL(Map<Key, Item> storage){
		this.repository = storage; 
	}
	
	public void setGarbageCollector(GarbageCollectorScheduler gc){
		if(null == gc)
			throw new NullPointerException();
		
		this.gc = gc;
	}
	
	public void add(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		synchronized(repository){
			blowIfItemExists(item);
			repository.put(item.getKey(), item);
			gc.getGCRef().monitor(item.getKey(), item.getExpire());
		}
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		DBStats.INSTANCE.getStorage().incrementNoItems();
	}


	public void append(Item item) throws NotFoundException, StorageException {
		checkItem(item);
		Item prevstored = null;
		synchronized(repository){
			prevstored = getPreviousStored(item);
			if(null != prevstored){
				Item newItem = new Item(prevstored.getKey(), concatData(prevstored, item), prevstored.getFlags(), item.getExpire());
				repository.put(prevstored.getKey(), newItem);
				gc.getGCRef().replace(item.getKey(), newItem.getExpire(), prevstored.getExpire());
			}
		}
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
	}

	public void prepend(Item item) throws NotFoundException, StorageException {
		checkItem(item);
		Item prevstored = null;
		synchronized(repository){
			prevstored = getPreviousStored(item);
			if(null != prevstored){
				Item newItem = new Item(prevstored.getKey(), concatData(item, prevstored), prevstored.getFlags(), item.getExpire());
				repository.put(prevstored.getKey(), newItem);
				gc.getGCRef().replace(item.getKey(), newItem.getExpire(), prevstored.getExpire());
			}
		}
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
	}

	public void replace(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		int oldSize = 0;
		
		synchronized(repository){
			Item prevItem = getItemAndDestroyItIfExpired(item.getKey());
			if (null == prevItem)
				throw new NotStoredException();
			oldSize = size(repository.put(item.getKey(), item).getData());
			gc.getGCRef().replace(item.getKey(), item.getExpire(), prevItem.getExpire());
		}

		DBStats.INSTANCE.getStorage().delBytes(oldSize);
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
	}

	public void delete(Key id) throws NotFoundException, StorageException {
		checkValidId(id);
		int oldSize = 0;

		synchronized(repository){
			blowIfItemDoesNotExists(id);
			Item oldItem = repository.remove(id);
			oldSize = size(oldItem.getData());
			gc.getGCRef().stop(id, oldItem.getExpire());
		}
		
		DBStats.INSTANCE.getStorage().delBytes(oldSize);
		DBStats.INSTANCE.getStorage().decrementNoItems();
	}

	public List<Item> get(List<Key> ids) throws StorageException {
		checkValidIds(ids);
		List<Item> resp = new LinkedList<Item>();
		for (Key k : ids) {
			synchronized(repository){
				resp.add(getItemAndDestroyItIfExpired(k));
			}
		}

		return resp;
	}

	public void set(Item item) throws StorageException {
		checkItem(item);
		Item olditem = null;
		synchronized(repository) {
			olditem = repository.put(item.getKey(), item);
			gc.getGCRef().monitor(item.getKey(), item.getExpire());
		}

		if(null != olditem){
			DBStats.INSTANCE.getStorage().delBytes(size(olditem.getData()));
			DBStats.INSTANCE.getStorage().decrementNoItems();
		}

		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		DBStats.INSTANCE.getStorage().incrementNoItems();
	}

	public void swap(Item item, String transactionid) throws NotFoundException, ExistsException,
			StorageException {
		checkItem(item);
		if(null == transactionid || transactionid.length() == 0)
			throw new StorageException("No CAS");
		
		synchronized(repository){
			Item prevItem = getItemAndDestroyItIfExpired(item.getKey());
			if(null == prevItem)
				throw new NotFoundException();
			if(!prevItem.generateCAS().compareTo(transactionid))
				throw new ExistsException();
			
			gc.getGCRef().replace(item.getKey(), item.getExpire(), prevItem.getExpire());		
			DBStats.INSTANCE.getStorage().delBytes(size(repository.put(item.getKey(), item).getData()));
		}
		
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
	}

	public Item decrease(Key id, String value) throws NotFoundException,
			StorageException {
		synchronized(repository){
			Item oldItem = blowIdInvalidIdOrWrongValue(id, value);
			String data = getDataAsString(oldItem.getData());
			Item newItem = new Item(oldItem.getKey(), BigDecrementer.decr(data, value).getBytes(), oldItem.getFlags());
			repository.put(oldItem.getKey(), newItem);			
			return newItem;
		}
	}

	public Item increase(Key id, String value) throws NotFoundException,
			StorageException {
		synchronized(repository){	
			Item oldItem = blowIdInvalidIdOrWrongValue(id, value);
			String data = getDataAsString(oldItem.getData());
			Item newItem = new Item(oldItem.getKey(), BigIncrementer.incr(data, value).getBytes(), oldItem.getFlags());
			repository.put(oldItem.getKey(), newItem);			
			return newItem;
		}
	}
	
	public void flush(Long time) {
		synchronized(repository){
			repository.clear();
			gc.getGCRef().flush();
		}
		DBStats.INSTANCE.getStorage().reset();
	}
	
	public void expire(Key k) {
		if(null == k)
			throw new NullPointerException("Key is null");

		synchronized(repository){
			Item item = repository.get(k);
			if(null == item)
				return;
			if(item.isExpired())
				repository.remove(k);
		}
	}

	private String getDataAsString(byte[] data) {
		return (null == data) ? "" : new String(data);
	}

	private void checkItem(Item item) throws StorageException {
		if (null == item)
			throw new StorageException("Null item");
		if (item.isExpired())
			throw new StorageException("Item is expired");
	}

	private void blowIfItemExists(Item item) throws NotStoredException {
		Item storedItem = getItemAndDestroyItIfExpired(item.getKey());
		if (null != storedItem)
			throw new NotStoredException();
	}

	private Item blowIfItemDoesNotExists(Key k) throws NotFoundException {
		Item storedItem = getItemAndDestroyItIfExpired(k);

		if (null == storedItem)
			throw new NotFoundException();
		
		return storedItem;
	}

	private boolean noInternalData(Item item) {
		return noBinaryData(item.getData());
	}

	private boolean noBinaryData(byte[] data) {
		return null == data || 0 == data.length;
	}

	private Item getPreviousStored(Item item) throws StorageException,
			NotFoundException {
		Item prevItem = getItemAndDestroyItIfExpired(item.getKey());
		
		if (null == prevItem)
			throw new NotFoundException();

		if (noInternalData(item))
			return null;

		return prevItem;
	}

	private byte[] concatData(Item prefix, Item postfix) {
		byte[] concatdata = new byte[computeNewInternalDataLength(prefix
				.getData(), postfix.getData())];

		int cursor = 0;

		if (!noInternalData(prefix)) {
			cursor = prefix.getData().length;
			System.arraycopy(prefix.getData(), 0, concatdata, 0, prefix
					.getData().length);
		}

		if (!noInternalData(postfix)) {
			System.arraycopy(postfix.getData(), 0, concatdata, cursor, postfix
					.getData().length);
		}

		return concatdata;
	}

	private Item blowIdInvalidIdOrWrongValue(Key id, String value) throws StorageException,
			NotFoundException {
		checkValidId(id);
		if (null == value)
			throw new StorageException("Null item");
		return blowIfItemDoesNotExists(id);
	}

	private int computeNewInternalDataLength(byte[] prefix, byte[] postfix) {
		int length = noBinaryData(prefix) ? 0 : prefix.length;
		return length + (noBinaryData(postfix) ? 0 : postfix.length);
	}

	private void checkValidIds(List<Key> ids) throws StorageException {
		if (null == ids || 0 == ids.size())
			throw new StorageException("No valid ids");
	}

	private void checkValidId(Key id) throws StorageException {
		if (null == id)
			throw new StorageException("No valid id");
	}
	
	private int size(byte[] data) {
		if(null == data)
			return 0;
		return data.length;
	}

	private Item getItemAndDestroyItIfExpired(Key key){
		Item item = repository.get(key);
		if(null == item)
			return null;
		if(item.isExpired()){
			repository.remove(key);
			gc.getGCRef().stop(key, item.getExpire());
			return null;
		}
		return item;
	}
}
