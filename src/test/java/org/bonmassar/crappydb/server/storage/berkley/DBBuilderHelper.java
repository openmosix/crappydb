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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;

import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.EnvironmentConfig;

public class DBBuilderHelper {
	private StorageAccessLayer um;
	
	public static class HelperPair {
		public EnvironmentConfig envConfig;
		public DatabaseConfig dbConfig;
		public static final String dbpath = "/tmp/test-crappydb-test";
	}
	
	public StorageAccessLayer build() throws ParseException {
		assertTrue((new File(HelperPair.dbpath)).mkdirs());
		Configuration.INSTANCE.parse(new String[]{"-d", HelperPair.dbpath});
		return um = SALFactory.newInstance(SALFactory.Catalogue.BERKLEY_FIXED_RATE_GC);
	}
	
	public HelperPair createSettingForMock() {
		HelperPair pair = new HelperPair();
		pair.envConfig = new EnvironmentConfig();
		pair.envConfig.setTransactional(true);
		pair.envConfig.setAllowCreate(true);
		pair.dbConfig = new DatabaseConfig();
		pair.dbConfig.setTransactional(true);
		pair.dbConfig.setAllowCreate(true);
		pair.dbConfig.setSortedDuplicates(true);
		return pair;
	}
	
	public void clean() throws StorageException {
		if(null != um){
			um.flush(0L);
			um.close();
		}
		TestBerkleyFactory.erase(new File(HelperPair.dbpath));
	}
}
