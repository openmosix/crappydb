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

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.storage.TestAddItem;
import org.junit.After;
import org.junit.Before;

public class TestBerkleyAddItem extends TestAddItem {
	
	private DBBuilderHelper builder;
	
	@Before
	public void setUp() throws ParseException{
		builder = new DBBuilderHelper();
		um = builder.build(); 
	}
	
	@After
	public void tearDown() {
		builder.clean();
	}

}
