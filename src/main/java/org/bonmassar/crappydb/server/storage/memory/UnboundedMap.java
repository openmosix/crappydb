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
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.utils.BigDecrementer;
import org.bonmassar.crappydb.utils.BigIncrementer;

public class UnboundedMap implements StorageAccessLayer {

	protected Map<Key, Item> repository;

	public UnboundedMap() {
		repository = new HashMap<Key, Item>();
	}

	public void add(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		blowIfItemExists(item);
		repository.put(item.getKey(), item);
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		DBStats.INSTANCE.getStorage().incrementNoItems();
	}


	public void append(Item item) throws NotFoundException, StorageException {
		Item prevstored = getPreviousStored(item);
		if (null != prevstored){
			prevstored.setData(concatData(prevstored, item));
			DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		}
	}

	public void prepend(Item item) throws NotFoundException, StorageException {
		Item prevstored = getPreviousStored(item);
		if (null != prevstored){
			prevstored.setData(concatData(item, prevstored));
			DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
		}
	}

	public void replace(Item item) throws NotStoredException, StorageException {
		checkItem(item);
		if (!repository.containsKey(item.getKey()))
			throw new NotStoredException();

		DBStats.INSTANCE.getStorage().delBytes(size(repository.put(item.getKey(), item).getData()));
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
	}

	public void delete(Key id) throws NotFoundException, StorageException {
		checkValidId(id);
		blowIfItemDoesNotExists(id);
		DBStats.INSTANCE.getStorage().delBytes(size(repository.remove(id).getData()));
		DBStats.INSTANCE.getStorage().decrementNoItems();
	}

	public List<Item> get(List<Key> ids) throws StorageException {
		checkValidIds(ids);
		List<Item> resp = new LinkedList<Item>();
		for (Key k : ids) {
			resp.add(repository.get(k));
		}

		return resp;
	}

	public void set(Item item) throws StorageException {
		checkItem(item);
		Item olditem = repository.put(item.getKey(), item);
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
		if(!repository.containsKey(item.getKey()))
			throw new NotFoundException();
		if(!repository.get(item.getKey()).generateCAS().compareTo(transactionid))
			throw new ExistsException();
		
		DBStats.INSTANCE.getStorage().delBytes(size(repository.put(item.getKey(), item).getData()));
		DBStats.INSTANCE.getStorage().addBytes(size(item.getData()));
	}

	public Item decrease(Key id, String value) throws NotFoundException,
			StorageException {
		Item it = getValidEntry(id, value);
		String data = getDataAsString(it.getData());
		it.setData(BigDecrementer.decr(data, value).getBytes());
		return it;
	}

	public Item increase(Key id, String value) throws NotFoundException,
			StorageException {
		Item it = getValidEntry(id, value);
		String data = getDataAsString(it.getData());
		it.setData(BigIncrementer.incr(data, value).getBytes());
		return it;
	}
	
	public void flush(Long time) {
		repository.clear();
		DBStats.INSTANCE.getStorage().reset();
	}

	private String getDataAsString(byte[] data) {
		return (null == data) ? "" : new String(data);
	}

	private void checkItem(Item item) throws StorageException {
		if (null == item)
			throw new StorageException("Null item");
	}

	private void blowIfItemExists(Item item) throws NotStoredException {
		if (repository.containsKey(item.getKey()))
			throw new NotStoredException();
	}

	private void blowIfItemDoesNotExists(Key k) throws NotFoundException {
		if (!repository.containsKey(k))
			throw new NotFoundException();
	}

	private boolean noInternalData(Item item) {
		return noBinaryData(item.getData());
	}

	private boolean noBinaryData(byte[] data) {
		return null == data || 0 == data.length;
	}

	private Item getPreviousStored(Item item) throws StorageException,
			NotFoundException {
		checkItem(item);
		if (!repository.containsKey(item.getKey()))
			throw new NotFoundException();

		if (noInternalData(item))
			return null;

		return repository.get(item.getKey());
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

	private Item getValidEntry(Key id, String value) throws StorageException,
			NotFoundException {
		checkValidId(id);
		if (null == value)
			throw new StorageException("Null item");
		blowIfItemDoesNotExists(id);

		Item it = repository.get(id);
		return it;
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


}
