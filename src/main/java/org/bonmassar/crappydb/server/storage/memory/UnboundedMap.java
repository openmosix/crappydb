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

import java.util.List;
import java.util.Map;

import org.bonmassar.crappydb.server.exceptions.ExistsException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import java.util.concurrent.ConcurrentHashMap;

public class UnboundedMap implements StorageAccessLayer {

	private Map<Key, Item> repository;
	
	public UnboundedMap () {
		repository = new ConcurrentHashMap<Key, Item>();
	}
	
	public void add(Item item) throws NotStoredException, StorageException {
		// TODO Auto-generated method stub

	}

	public void append(Item item) throws StorageException {
		// TODO Auto-generated method stub

	}

	public Item decrease(Key id, Long value) throws NotFoundException,
			StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(Key id) throws NotFoundException, StorageException {
		// TODO Auto-generated method stub

	}

	public List<Item> get(List<Key> ids) throws NotFoundException,
			StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Item increase(Key id, Long value) throws NotFoundException,
			StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	public void prepend(Item item) throws StorageException {
		// TODO Auto-generated method stub

	}

	public void replace(Item item) throws NotStoredException, StorageException {
		// TODO Auto-generated method stub

	}

	public void set(Item item) throws StorageException {
		// TODO Auto-generated method stub

	}

	public void swap(Item item) throws NotFoundException, ExistsException,
			StorageException {
		// TODO Auto-generated method stub

	}

}
