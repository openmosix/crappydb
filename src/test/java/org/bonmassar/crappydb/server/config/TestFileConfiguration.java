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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

public class TestFileConfiguration {
	
	private CLIConfiguration config;
	
	@Before
	public void setUp() throws FileNotFoundException, IOException, ParseException {
		config = ConfigurationBuilder.getConfig(new String[] {"--file=src/test/resources/crappytest.conf"});
	}

	@Test
	public void testBufferSize() throws ParseException{
		assertEquals(8 * 1024 , config.getBufferSize());
	}
	
	@Test
	public void testGetEngineThreads() throws ParseException {
		assertEquals(180 , config.getEngineThreads());		
	}
	
	@Test
	public void testPort() throws ParseException {
		assertEquals(999 , config.getServerPort());				
	}
	
	@Test
	public void testGetMaxPayloadSize() throws ParseException {
		assertEquals(64*1024*1024 , config.getMaxPayloadSize());						
	}
	
	@Test
	public void testGetHostname() {
		assertNull(config.getHostname());
	}
	
	@Test
	public void testIsDumpParams() {
		assertFalse(config.isDumpParams());
	}
	
	@Test
	public void testIsHelpMessage() {
		assertFalse(config.isHelpMessage());
	}
}
