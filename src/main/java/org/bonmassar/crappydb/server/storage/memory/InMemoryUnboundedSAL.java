package org.bonmassar.crappydb.server.storage.memory;

import java.util.List;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

public class InMemoryUnboundedSAL implements StorageAccessLayer {
	
	private UnboundedMap storage;
	private TimeQueue expire;

	public void add(Item item) throws NotStoredException, StorageException {
		throw new StorageException("Not Implemented.");
	}

	public void append(Item item) throws StorageException {
		throw new StorageException("Not Implemented.");
	}

	public Item decrease(Key id, Long value) throws NotFoundException,
			StorageException {
		throw new StorageException("Not Implemented.");
	}

	public void delete(Key id) throws NotFoundException, StorageException {
		throw new StorageException("Not Implemented.");
	}

	public List<Item> get(List<Key> ids) throws NotFoundException,
			StorageException {
		throw new StorageException("Not Implemented.");
	}

	public Item increase(Key id, Long value) throws NotFoundException,
			StorageException {
		throw new StorageException("Not Implemented.");
	}

	public void prepend(Item item) throws StorageException {
		throw new StorageException("Not Implemented.");
	}

	public void replace(Item item) throws NotStoredException, StorageException {
		throw new StorageException("Not Implemented.");
	}

	public void set(Item item) throws StorageException {
		throw new StorageException("Not Implemented.");
	}

	public void swap(Item item) throws NotFoundException, ExistsException,
			StorageException {
		throw new StorageException("Not Implemented.");
	}

}
