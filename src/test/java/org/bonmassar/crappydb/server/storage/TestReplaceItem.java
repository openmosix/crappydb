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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public abstract class TestReplaceItem {

	protected StorageAccessLayer um;
	private Item it;

	public void setUp() throws ParseException{
		it = new Item(new Key("key"), "this is payload".getBytes(), 0);
	}
	
	@Test
	public void testReplaceNoValidItem() {
		try {
			um.replace(null);
		} catch (NotStoredException e) { } 
		catch (StorageException e) { return ; }
		fail();
	}
	
	@Test
	public void testReplaceNoPreviousItem() {
		try {
			um.replace(it);
		} catch (NotStoredException e) {
			return;
		} catch (StorageException e) { }
		fail();
	}
	
	@Test
	public void testReplaceRainbow() throws NotStoredException, StorageException {
		um.add(it);
		it = new Item(new Key("key"), "this is a new payload".getBytes(), 0);
		um.replace(it);
		List<Item> res = um.get(Arrays.asList(new Key("key")));
		assertEquals("this is a new payload", new String(res.get(0).getData()) );
	}

}
