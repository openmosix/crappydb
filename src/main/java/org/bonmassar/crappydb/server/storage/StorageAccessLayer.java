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
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.data.Item;

public interface StorageAccessLayer {

	void set(Item item) throws StorageException;
	
	void add(Item item) throws NotStoredException, StorageException;
	
	void replace(Item item) throws NotStoredException, StorageException;
	
	void append(Item item) throws NotFoundException, StorageException;
	
	void prepend(Item item) throws NotFoundException, StorageException;
	
	void swap(Item item) throws NotFoundException, ExistsException, StorageException;
	
	List<Item> get(List<Key> ids) throws StorageException;
	
	void delete(Key id) throws NotFoundException, StorageException;
	
	Item increase(Key id, Long value) throws NotFoundException, StorageException;
	
	Item decrease(Key id, Long value) throws NotFoundException, StorageException;
	
}
