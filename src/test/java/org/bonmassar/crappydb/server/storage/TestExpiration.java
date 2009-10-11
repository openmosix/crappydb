
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public abstract class TestExpiration {
	protected StorageAccessLayer um;
	
	@Test
	public void testExpireRainbow() throws StorageException, InterruptedException {
		um.set(new Item(new Key("Zzz"), "payload".getBytes(), 12, 2));
		Thread.sleep(3000);
		Item result = um.expire(new Key("Zzz"));
		assertNotNull(result);
	}
	
	@Test
	public void testExpireButItemNotExpired() throws StorageException, InterruptedException {
		um.set(new Item(new Key("Zzz"), "payload".getBytes(), 12));
		Item result = um.expire(new Key("Zzz"));
		assertNull(result);
	}
	
	@Test
	public void testExpireButNotStored() throws StorageException, InterruptedException {
		Item result = um.expire(new Key("Zzz"));
		assertNull(result);
	}
}
