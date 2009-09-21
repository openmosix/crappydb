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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

public class ConfigurationBuilder {
	
	private static Logger logger = Logger.getLogger(ConfigurationBuilder.class);
	
	public static CLIConfiguration getConfig(String[] args) throws ParseException {
		CLIConfiguration cliconfig = new CLIConfiguration();
		cliconfig.parse(args);
		
		String filename = cliconfig.getConfigurationFileName();
		try {
			 return new FileConfiguration(filename);
		} catch (FileNotFoundException e) {
			logger.warn(String.format("Cannot find %s configuration file, continuing using command line config", filename));
			cliconfig.setNoConfigFileFound();
			return cliconfig;
		} catch (IOException e) {
			logger.fatal(String.format("IO errors reading %s config file", filename));
			throw new RuntimeException(e);
		}
	}
	
}
