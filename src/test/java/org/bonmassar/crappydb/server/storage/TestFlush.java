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
 *  
 * The original version of this class comes from Christian d'Heureuse, 
 * Inventec Informatik AG, Switzerland.
 * Home page: http://www.source-code.biz
 * Source code http://www.source-code.biz/snippets/java/2.htm
 *  
 */

package org.bonmassar.crappydb.server.storage;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Test;

public abstract class TestFlush {

	protected StorageAccessLayer um;

	@Test
	public void testFlush() throws NotStoredException, StorageException{
		um.add(new Item(new Key("Terminenzio1"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio2"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio3"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio4"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio5"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio6"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio7"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio8"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio9"), "werwer".getBytes(), 33));
		um.add(new Item(new Key("Terminenzio0"), "werwer".getBytes(), 33));

		for(int i = 0; i < 10; i++)
			assertEquals(new Key("Terminenzio"+i), um.get(Arrays.asList(new Key("Terminenzio"+i))).get(0).getKey());
	
		um.flush(200L);

		for(int i = 0; i < 10; i++)
			assertEquals(0, um.get(Arrays.asList(new Key("Terminenzio"+i))).size());

	}
	
}
