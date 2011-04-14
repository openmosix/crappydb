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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class TestConfigurationBuilder {

	@Test
	public void testWithoutConfigFile() throws ParseException {
		CLIConfiguration config = new ConfigurationBuilder().getConfig(new String[]{"-p 128"});
		assertFalse(config instanceof FileConfiguration);
	}
	
	@Test
	public void testWithConfigFile() throws ParseException {
		CLIConfiguration config = new ConfigurationBuilder().getConfig(new String[]{"--file=src/test/resources/crappytest.conf"});
		assertTrue(config instanceof FileConfiguration);
	}
	
	@Test
	public void testWithConfigFileButFileDoesNotExists() throws ParseException {
		CLIConfiguration config = new ConfigurationBuilder().getConfig(new String[]{"--file=crappycrappy.conf"});
		assertFalse(config instanceof FileConfiguration);
	}
	
	@Test
	public void testEmptyCreation() {
		new ConfigurationBuilder();	//do not throw exception
	}
	
	@Test
	public void testIOException() throws ParseException {
		class ConfigIOException extends ConfigurationBuilder {
			@Override
			protected FileConfiguration getFile(String filename)
					throws FileNotFoundException, IOException {
				throw new IOException("BOOM");
			}
		}
		
		try {
			new ConfigIOException().getConfig(new String[]{"--file=src/test/resources/crappytestbin.conf"});
		} catch (RuntimeException e) {
			return;
		}
		fail();
	}
}
