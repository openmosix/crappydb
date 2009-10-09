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

package org.bonmassar.crappydb.server.storage.memory;

import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.TestDeleteItem;
import org.junit.Before;

public class TestUnboundedMapDeleteItem  extends TestDeleteItem{

	
	@Before
	public void setUp(){
		um = SALFactory.newInstance(SALFactory.Catalogue.INMEMORY_UNBOUNDED_FIXED_RATE_GC);
	}

}
