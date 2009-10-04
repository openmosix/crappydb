package org.bonmassar.crappydb.server.storage.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.PAL;
import org.bonmassar.crappydb.server.storage.data.DeleteItem;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.utils.BigDecrementer;
import org.bonmassar.crappydb.utils.BigIncrementer;

public class CollectionPAL extends PAL {

	final Map<Key, Item> repository;
	
	public CollectionPAL(HashMap<Key, Item> repository) {
		this.repository = repository;
	}

	public synchronized void add(Item item) throws NotStoredException, StorageException {
		blowIfItemExists(null, item);
		repository.put(item.getKey(), item);
	}

	public synchronized Item append(Item item) throws NotFoundException, StorageException {
		Item prevstored = getPreviousStored(null, item);
		if(null != prevstored){
			Item newItem = new Item(prevstored.getKey(), concatData(prevstored, item), prevstored.getFlags(), item.getExpire());
			repository.put(prevstored.getKey(), newItem);
		}
		return prevstored;
	}

	public synchronized Item decrease(Key id, String value) throws NotFoundException,
			StorageException {
		Item oldItem = blowIdInvalidIdOrWrongValue(null, id, value);
		String data = getDataAsString(oldItem.getData());
		Item newItem = new Item(oldItem.getKey(), BigDecrementer.decr(data, value).getBytes(), oldItem.getFlags());
		repository.put(oldItem.getKey(), newItem);			
		return newItem;
	}

	public synchronized Item delete(Key id, Long time) throws NotFoundException,
			StorageException {
		blowIfItemDoesNotExists(null, id);

		Item oldItem = repository.remove(id);
		if(null != time && -1L != time){
			Item newItem = new DeleteItem( oldItem );
			repository.put(id, newItem);
		}
		return oldItem;
	}

	public synchronized void flush(Long time) {
		repository.clear();
	}

	public synchronized List<Item> get(List<Key> ids) throws StorageException {
		List<Item> resp = new LinkedList<Item>();
		for (Key k : ids) {
			Item it = getItemAndDestroyItIfExpired(null, k);
			if(!isDeleted(it))
				resp.add(it);
		}
		return resp;
	}

	public synchronized Item increase(Key id, String value) throws NotFoundException,
			StorageException {
		Item oldItem = blowIdInvalidIdOrWrongValue(null, id, value);
		String data = getDataAsString(oldItem.getData());
		Item newItem = new Item(oldItem.getKey(), BigIncrementer.incr(data, value).getBytes(), oldItem.getFlags());
		repository.put(oldItem.getKey(), newItem);			
		return newItem;
	}

	public synchronized Item prepend(Item item) throws NotFoundException, StorageException {
		Item prevstored = getPreviousStored(null, item);
		if(null != prevstored){
			Item newItem = new Item(prevstored.getKey(), concatData(item, prevstored), prevstored.getFlags(), item.getExpire());
			repository.put(prevstored.getKey(), newItem);
		}
		return prevstored;
	}

	public synchronized Item replace(Item item) throws NotStoredException, StorageException {
		Item prevItem = getItemAndDestroyItIfExpired(null, item.getKey());
		if (null == prevItem || isDeleted(prevItem))
			throw new NotStoredException();
		return repository.put(item.getKey(), item);
	}

	public Item set(Item item) throws StorageException {
		return repository.put(item.getKey(), item);
	}

	public synchronized Item swap(Item item, String CASId) throws NotFoundException,
			ExistsException, StorageException {
		Item prevItem = getItemAndDestroyItIfExpired(null, item.getKey());
		if(null == prevItem || isDeleted(prevItem))
			throw new NotFoundException();
		if(!prevItem.generateCAS().compareTo(CASId))
			throw new ExistsException();

		return repository.put(item.getKey(), item);
	}	

	public synchronized Item expire(Key k) {
		Item item = repository.get(k);
		if(null == item)
			return null;
		if(!item.isExpired())
			return null;
		
		return repository.remove(k);	
	}

	protected Item getItemAndDestroyItIfExpired(Object lock, Key key){
		Item item = repository.get(key);
		if(null == item || !item.isExpired())
			return item;
		
		repository.remove(key);
		
		return null;
	}

	public void close() {}
}
