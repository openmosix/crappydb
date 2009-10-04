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

package org.bonmassar.crappydb.server.storage.berkley.data;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

public class BerkleyFactory {

	/**
	 * Create a BerkleyPAL db specifying the fs path
	 * @param dbpath	The path to the database
	 * @return	An handler for BerkleyDB
	 * @throws DatabaseException 
	 * @throws EnvironmentLockedException 
	 */
	public BerkleyPAL newInstance(String dbpath) throws EnvironmentLockedException, DatabaseException{
		EnvironmentConfig envConfig = new EnvironmentConfig(); 
		envConfig.setAllowCreate(true); 
		envConfig.setTransactional(true); 
		Environment env = new Environment(new File(dbpath), envConfig);
		
		StoreConfig storeConfig = new StoreConfig(); 
		storeConfig.setAllowCreate(true); 
		storeConfig.setTransactional(true); 
		EntityStore store = new EntityStore(env, "ItemStore", storeConfig); 

		return new BerkleyPAL(env, store);
	}
	
}
