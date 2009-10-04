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

import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.DeleteItem;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

public abstract class PAL implements PhysicalAccessLayer {

	protected StorageAccessLayer sal;
	
	public void setSAL(StorageAccessLayer sal){
		this.sal = sal;
	}
	
	protected Item getPreviousStored(Object lock, Item item) throws StorageException,
	NotFoundException {
		Item prevItem = getItemAndDestroyItIfExpired(lock, item.getKey());

		if (null == prevItem || isDeleted(prevItem))
			throw new NotFoundException();

		if (noInternalData(item))
			return null;

		return prevItem;
	}
	
	protected Item blowIfItemDoesNotExists(Object lock, Key k) throws NotFoundException {
		Item storedItem = getItemAndDestroyItIfExpired(lock, k);

		if (null == storedItem)
			throw new NotFoundException();
		
		return storedItem;
	}
	
	protected boolean isDeleted(Item storedItem) {
		return storedItem instanceof DeleteItem;
	}
	
	protected String getDataAsString(byte[] data) {
		return (null == data) ? "" : new String(data);
	}

	protected void blowIfItemExists(Object lock, Item item) throws NotStoredException {
		Item storedItem = getItemAndDestroyItIfExpired(lock, item.getKey());
		if (null != storedItem || isDeleted(storedItem))
			throw new NotStoredException();
	}

	protected byte[] concatData(Item prefix, Item postfix) {
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

	private boolean noInternalData(Item item) {
		return noBinaryData(item.getData());
	}

	private boolean noBinaryData(byte[] data) {
		return null == data || 0 == data.length;
	}
	
	private int computeNewInternalDataLength(byte[] prefix, byte[] postfix) {
		int length = noBinaryData(prefix) ? 0 : prefix.length;
		return length + (noBinaryData(postfix) ? 0 : postfix.length);
	}

	protected Item blowIdInvalidIdOrWrongValue(Object lock, Key id, String value) throws StorageException,
		NotFoundException {
		if (null == value)
			throw new StorageException("Null item");
		return blowIfItemDoesNotExists(lock, id);
	}
	
	protected abstract Item getItemAndDestroyItIfExpired(Object lock, Key key);

}
