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
import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;

public class DBBuilderHelper {
	private static final String dbpath = "/tmp/test-crappydb-test";
	private StorageAccessLayer um;
	
	public StorageAccessLayer build() throws ParseException {
		assertTrue((new File(dbpath)).mkdirs());
		Configuration.INSTANCE.parse(new String[]{"-d", dbpath});
		return um = SALFactory.newInstance(SALFactory.Catalogue.BERKLEY_FIXED_RATE_GC);
	}
	
	public void clean() {
		um.flush(0L);
		if(null != um)
			um.close();
		TestBerkleyFactory.erase(new File(dbpath));
	}
}
