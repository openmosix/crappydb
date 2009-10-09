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

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.storage.SALFactory.Catalogue;
import org.bonmassar.crappydb.server.storage.berkley.TestBerkleyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSALFactory extends TestCase {
	
	private static String dbpath = "/tmp/crappydb/db";
	private String path;
	
	@Before
	public void setUp() throws ParseException {
		path = dbpath+System.currentTimeMillis();
		assertTrue((new File(path)).mkdirs());
		Configuration.INSTANCE.parse(new String[]{"-d", path});
		new File(path).mkdirs();
	}
	
	@After
	public void tearDown() {
		TestBerkleyFactory.erase(new File(path));	
	}

	@Test
	public void testBuildInMemoryUnboundedWithFixedRateGC(){
		assertNotNull(SALFactory.newInstance(Catalogue.INMEMORY_UNBOUNDED_FIXED_RATE_GC));
	}
	
	@Test
	public void testBuildInMemoryUnboundedNoGC(){
		assertNotNull(SALFactory.newInstance(Catalogue.INMEMORY_UNBOUNDED_NO_GC));
	}
	
	@Test
	public void testBuildBerkleyFixedRateGc() {
		assertNotNull(SALFactory.newInstance(Catalogue.BERKLEY_FIXED_RATE_GC));
	}
	
	@Test
	public void testBuildBerkleyNoGc() {
		assertNotNull(SALFactory.newInstance(Catalogue.BERKLEY_NO_GC));
	}
	
	@Test
	public void testGetEnumFromString() {
		for(Catalogue c : Catalogue.values()){
			String c_string = c.toString();
			
			Catalogue result = Catalogue.parseString(c_string);
			assertEquals(c, result);
		}
	}
	
	@Test
	public void testGetEnumFromNullString() {
		try {
			Catalogue.parseString(null);
		} catch (NullPointerException e) {
			return;
		}
		fail();
	}
}
