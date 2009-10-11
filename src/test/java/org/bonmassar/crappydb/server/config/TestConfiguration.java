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

package org.bonmassar.crappydb.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.config.Configuration;
import org.junit.Test;

public class TestConfiguration {

	@Test
	public void testIsMonoInstance() {
		assertEquals(1, Configuration.values().length);
	}
	
	@Test
	public void testValueOfInstance() {
		assertNotNull(Configuration.valueOf("INSTANCE"));
	}
	
	@Test
	public void testServerPort() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--port=777"});
		assertEquals(777, Configuration.INSTANCE.getServerPort());
	}
	
	@Test
	public void testServerPortLongFormat() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"-p 777"});
		assertEquals(777, Configuration.INSTANCE.getServerPort());
	}
	
	@Test
	public void testServerPortInvalidFormat() throws ParseException {
		try{
			Configuration.INSTANCE.parse(new String[]{"-p meee777"});
		}catch(ParseException pe){
			return;
		}
		fail();
	}
	
	@Test
	public void testInvalidCatalogue() throws ParseException {
		try{
			Configuration.INSTANCE.parse(new String[]{"--storage", "terminenzio"});
		}catch(ParseException pe){
			return;
		}
		fail();
	}
	
	@Test
	public void testDetaultServerPort() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{""});
		assertEquals(11211, Configuration.INSTANCE.getServerPort());		
	}
	
	@Test
	public void testDetaultServerPortNullParams() throws ParseException {
		Configuration.INSTANCE.parse(null);
		assertEquals(11211, Configuration.INSTANCE.getServerPort());		
	}
	
	@Test
	public void testHost() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"-h localhost"});
		assertEquals("localhost", Configuration.INSTANCE.getHostname());		
	}
	
	@Test
	public void testHostLongFormat() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--hostname=localhost"});
		assertEquals("localhost", Configuration.INSTANCE.getHostname());		
	}
	
	@Test
	public void testHostInvalidFormat() throws ParseException {
		try{
			Configuration.INSTANCE.parse(new String[]{"-h"});
		}catch(MissingArgumentException meg){
			return;
		}
		fail();
	}
	
	@Test
	public void testHelpLongFormat() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--help"});
		assertTrue(Configuration.INSTANCE.isHelpMessage());		
	}
	
	@Test
	public void testDump() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--dump"});
		assertTrue(Configuration.INSTANCE.isDumpParams());	
	}
	
	@Test
	public void testVersion() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--version"});
		assertTrue(Configuration.INSTANCE.isVersion());	
	}
	
	@Test
	public void testVersionWithLong() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"-v"});
		assertTrue(Configuration.INSTANCE.isVersion());	
	}
	
	@Test
	public void testThreads() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"-t80"});
		assertEquals(80, Configuration.INSTANCE.getEngineThreads());	
	}
	
	@Test
	public void testThreadsWithLong() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--threads=80"});
		assertEquals(80, Configuration.INSTANCE.getEngineThreads());	
	}
		
	@Test
	public void testBufferSizeWithLong() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--buffer-size=29090"});
		assertEquals(29090, Configuration.INSTANCE.getBufferSize());	
	}
	
	@Test
	public void testMaxPayloadSizeWithLong() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"--max-payload-size=301010"});
		assertEquals(301010, Configuration.INSTANCE.getMaxPayloadSize());	
	}
	
	@Test
	public void testGetOptions() throws ParseException {
		Configuration.INSTANCE.parse(new String[]{"-v", "-p128", "-t12"});
		String params = Configuration.INSTANCE.getConfigParams();
		
		assertEquals("file CommandLine\ndump off\nhelp off\nversion on\nhostname *\nport 128\nthreads 12\nbuffer-size 8192\nmax-payload-size 67108864\nstorage unbounded-memory\ndbpath /var/crappydb/db\n", params);
	}
	
	
	@Test
	public void testHelpMenu() {
		Configuration.INSTANCE.generateHelp();
	}
	
	@Test
	public void testReset() throws ParseException {
		Configuration.INSTANCE.parse(new String[0]);
	}
}
