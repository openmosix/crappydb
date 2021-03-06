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

package org.bonmassar.crappydb.server.storage.berkley;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
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

import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

class BerkleyPAL extends PAL {

	private final static Logger logger = Logger.getLogger(BerkleyPAL.class);
	
	final PrimaryIndex<String, ItemEntity> repository; 
	final Environment environment;
	final EntityStore store;
	
	public BerkleyPAL(EntityStore store) throws DatabaseException{
		this.repository = store.getPrimaryIndex( String.class, ItemEntity.class );
		this.environment = store.getEnvironment();
		this.store = store;
	}
	
	public void add(Item item) throws NotStoredException, StorageException {
		Transaction txn = newTransaction();
		try{
			blowIfItemExists(txn, item);
			put(txn, item);
		} finally{
			commit(txn);
		}
	}

	public Item append(Item item) throws NotFoundException, StorageException {
		Transaction txn = newTransaction();
		try{
			Item prevstored = getPreviousStored(txn, item);
			if(null != prevstored){
				Item newItem = new Item(prevstored.getKey(), concatData(prevstored, item), prevstored.getFlags(), item.getExpire());
				put(txn, newItem);
			}
			return prevstored;
		} finally {
			commit(txn);
		}
	}

	public Item decrease(Key id, String value) throws NotFoundException,
			StorageException {
		Transaction txn = newTransaction();
		try{
			Item oldItem = blowIdInvalidIdOrWrongValue(txn, id, value);
			String data = getDataAsString(oldItem.getData());
			Item newItem = new Item(oldItem.getKey(), BigDecrementer.decr(data, value).getBytes(), oldItem.getFlags());
			put(txn, newItem);			
			return newItem;
		} finally {
			commit(txn);
		}
	}

	public Item delete(Key id, Long time) throws NotFoundException,
			StorageException {
		Transaction txn = newTransaction();
		try{
			blowIfItemDoesNotExists(txn, id);
	
			Item oldItem = delete(txn, id);
			if(null != time && -1L != time){
				put(txn, new DeleteItem( oldItem ));
			}
			return oldItem;
		} finally {
			commit(txn);
		}
	}

	public void flush(Long time) throws StorageException{
		Transaction txn = newTransaction();
		try{
			flushDB(txn);
		} finally {
			commit(txn);
		}
	}

	public List<Item> get(List<Key> ids) throws StorageException {
		Transaction txn = newTransaction();
		try{
			List<Item> resp = new LinkedList<Item>();
			for (Key k : ids) {
				Item it = getItemAndDestroyItIfExpired(txn, k);
				if(null != it && !isDeleted(it))
					resp.add(it);
			}
			return resp;
		} finally {
			commit(txn);
		}
	}

	public Item increase(Key id, String value) throws NotFoundException,
			StorageException {
		Transaction txn = newTransaction();
		try{
			Item oldItem = blowIdInvalidIdOrWrongValue(txn, id, value);
			String data = getDataAsString(oldItem.getData());
			Item newItem = new Item(oldItem.getKey(), BigIncrementer.incr(data, value).getBytes(), oldItem.getFlags());
			put(txn, newItem);			
			return newItem;
		} finally {
			commit(txn);
		}
	}

	public Item prepend(Item item) throws NotFoundException, StorageException {
		Transaction txn = newTransaction();

		try{
			Item prevstored = getPreviousStored(txn, item);
			if(null != prevstored){
				Item newItem = new Item(prevstored.getKey(), concatData(item, prevstored), prevstored.getFlags(), item.getExpire());
				put(txn, newItem);
			}
			return prevstored;
		} finally {
			commit(txn);
		}
	}

	public Item replace(Item item) throws NotStoredException, StorageException {
		Transaction txn = newTransaction();
		try{
			Item prevItem = getItemAndDestroyItIfExpired(txn, item.getKey());
			if (null == prevItem || isDeleted(prevItem))
				throw new NotStoredException();
			put(txn, item);
			return prevItem;
		} finally {
			commit(txn);
		}
	}

	public Item set(Item item) throws StorageException {
		Transaction txn = newTransaction();
		try{
			Item old = get(txn, item.getKey());
			put(txn, item);
			return old;
		} finally {
			commit(txn);
		}
	}

	public Item swap(Item item, String CASId) throws NotFoundException,
			ExistsException, StorageException {
		Transaction txn = newTransaction();
		try{
			Item prevItem = getItemAndDestroyItIfExpired(txn, item.getKey());
			if(null == prevItem || isDeleted(prevItem))
				throw new NotFoundException();
			if(!prevItem.generateCAS().compareTo(CASId))
				throw new ExistsException();

			put(txn, item);
			return prevItem;
		} finally {
			commit(txn);
		}
	}	

	public Item expire(Key k) {
		try{
			Transaction txn = newTransaction();
			try{	
				Item item = get(txn, k);
				if(null == item || !item.isExpired())
					return null;

				delete(txn, k);
				return item;
			} finally {
				commit(txn);
			}
		}catch(StorageException se){
			//swallowing
			return null;
		}
	}

	protected Item getItemAndDestroyItIfExpired(Object lock, Key key){
		if(null == key)
			return null;
		
		Transaction txn = (Transaction)lock;
	
		Item item;
		try {
			item = get(txn, key);
		} catch (StorageException e) {
			return null;
		}
		if(null == item || !item.isExpired())
			return item;
		
		try {
			delete(txn, item.getKey());
		} catch (StorageException e) {
			logger.error(String.format("Cannot delete expired key %s, error writing on db", item.getKey()), e);
			return null;
		}
		
		notifyEviction(item);
		return null;
	}
	
	public void close() {
		try {
			store.close();
		} catch (DatabaseException e) {
			logger.error("Cannot close the BerkleyDB storage", e);
		}
		try {
			environment.close();
		} catch (DatabaseException e) {
			logger.error("Cannot close the BerkleyDB environment", e);
		}
	}
	
	private Transaction newTransaction() throws StorageException {
		try {
			return environment.beginTransaction(null, null);
		} catch (DatabaseException e) {
			logger.error("Cannot open transaction", e);
			throw new StorageException("Cannot open transaction");
		}
	}

	private void commit(Transaction transaction) throws StorageException {
		try {
			if(null != transaction)
				transaction.commit();
			
		} catch (DatabaseException e) {
			logger.error("Cannot close transaction", e);
			throw new StorageException("Cannot close transaction");
		}
	}
	
	private void put(Transaction transaction, Item it) throws StorageException{
		try {
			repository.put(transaction, new ItemEntity(it));
		} catch (DatabaseException e) {
			logger.error("Error writing on database", e);
			throw new StorageException("Error writing on database");
		}
	}
	
	private Item delete(Transaction transaction, Key k) throws StorageException{
		try {
			ItemEntity old = repository.get(transaction, k.toString(), LockMode.RMW);
			repository.delete(transaction, k.toString());
			
			return old.toItem();
		} catch (DatabaseException e) {
			logger.error("Error deleting from database", e);
			throw new StorageException("Error deleting from database");
		}
	}
	
	private Item get(Transaction transaction, Key k) throws StorageException{
		try {
			ItemEntity old = repository.get(transaction, k.toString(), LockMode.RMW);
			if(null == old)
				return null;
			return old.toItem();
		} catch (DatabaseException e) {
			logger.error("Error writing on database", e);
			throw new StorageException("Error writing on database");
		}
	}
	
	private void flushDB(Transaction transaction) throws StorageException {
		try{
			EntityCursor<ItemEntity> cursor = repository.entities(transaction, CursorConfig.READ_COMMITTED);
			try {
				for (ItemEntity entity = cursor.first(); entity != null; entity = cursor.next())
					cursor.delete();
			} finally {
				cursor.close();
			}
		} catch(DatabaseException e){
			logger.error("Error flushing database", e);
			throw new StorageException("Error flushing database");			
		}
	}
}
