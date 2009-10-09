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

package org.bonmassar.crappydb.server.storage.berkley;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bonmassar.crappydb.server.storage.berkley.BerkleyFactory;
import org.junit.After;
import org.junit.Test;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.persist.EntityStore;

public class TestBerkleyFactory {

	private static final String path = "/var/tmp/test-crappydb-test";
	
	@After
	public void tearDown() {
		erase (new File(path));
	}
	
	
	@SuppressWarnings("static-access")
	@Test
	public void testBuildDB() throws EnvironmentLockedException, DatabaseException{
		BerkleyFactory factory = new BerkleyFactory();
		assertTrue((new File(path)).mkdirs());
		EntityStore store = factory.newInstance(path);
		assertNotNull(store);
		assertNotNull(store.getEnvironment());
		assertNotNull(store.getEnvironment().getConfig());
		assertEquals("ItemStore", store.getStoreName());
		store.close();
	}
	
	private boolean erase(File folder) {
        if (folder.isDirectory()) {
            String[] contents = folder.list();
            for (String content : contents) 
                if (!erase(new File(folder, content)))
                    return false;
        }
    
        return folder.delete();
    } 
}
