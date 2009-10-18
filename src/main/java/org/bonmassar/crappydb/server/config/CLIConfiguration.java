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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bonmassar.crappydb.server.storage.SALFactory.Catalogue;

class CLIConfiguration extends DefaultConfiguration {

	private CommandLine cli;
	private boolean configFileNotFound = false;
	
	public void generateHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "crappydbd", buildOptions() );
	}
	
	public void parse(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		
		cli = parser.parse( buildOptions(), args );
	}
	
	@Override
	public int getBufferSize() throws ParseException {
	    if( !cli.hasOption( BUFFSIZE ) )
	    	return super.getBufferSize();
	    
	    return toInt(cli.getOptionValue( BUFFSIZE).trim(), BUFFSIZE);
	}

	@Override
	public int getEngineThreads() throws ParseException {
		if( !cli.hasOption( THREADS ) )
	    	return super.getEngineThreads();
	    
		return toInt(cli.getOptionValue( THREADS ).trim(), THREADS);
	}

	@Override
	public String getHostname() {
	    if( !cli.hasOption( HOSTNAME ) )
	    	return super.getHostname();

	    return cli.getOptionValue( HOSTNAME ).trim();
	}

	@Override
	public int getMaxPayloadSize() throws ParseException {
		if( !cli.hasOption( MAXPAYLOADSIZE ) )
	    	return super.getMaxPayloadSize();
	
		return toInt( cli.getOptionValue( MAXPAYLOADSIZE ).trim(), MAXPAYLOADSIZE);
	}

	@Override
	public int getServerPort() throws ParseException {
	    if( !cli.hasOption( PORT ) )
	    	return super.getServerPort();

	    return toInt(cli.getOptionValue( PORT ).trim(), PORT);
	}

	@Override
	public String getConfigurationFileName() {
		if(configFileNotFound)
			return "CommandLine";
		
	    if( !cli.hasOption( FILE ) )
	    	return super.getConfigurationFileName();

	    return cli.getOptionValue( FILE ).trim();
	}
	
	@Override
	public String getDBPath() throws ParseException {
	    if( !cli.hasOption( DB ) )
	    	return super.getDBPath();

	    return cli.getOptionValue( DB ).trim();
	}

	public Catalogue getStorage() throws ParseException {
	    if( !cli.hasOption( STORAGE ) )
	    	return super.getStorage();

	    return fromCatalogue(cli.getOptionValue(STORAGE), STORAGE);
	}	
	
	public boolean isDumpParams() {
	    return cli.hasOption( DUMP );
	}

	public boolean isHelpMessage() {
	    return cli.hasOption( HELP );
	}

	public boolean isVersion() {
		return cli.hasOption( VERSION );
	}
	
	public boolean isUdp() {
		return cli.hasOption( UDP );
	}
	
	private Options buildOptions() {
		Options options = new Options();
		options.addOption( null, "help", false, "print this help message." );
		options.addOption( "d", "dbpath", true, String.format("the fs path where the db stores its data (def: %s).", DefaultConfiguration.DB.toString() ));
		options.addOption( "s", "storage", true, String.format("select the storage layer (e.g.: memory only, persistent db, etc.). Allowed values: %s", getStorageAllowedValues() ));
		options.addOption( "v", "version", false, "print server version." );
		options.addOption( null, "dump", false, "dump runtime config parameters in log files." );
		options.addOption( null, "buffer-size", true, "define the internal buffer size for IO operations. (def: 8K)" );
		options.addOption( null, "max-payload-size", true, "define the maximum payload size for a write operation (def: 64M)" );
		options.addOption( "t", "threads", true, "number of engine threads (def: number of processors x 2)." );
		options.addOption( "f", "file", true, "server configuration file (def: ./crappydb.conf)" );
		options.addOption( "h", "hostname", true, "bind the server to this hostname." );
		options.addOption( "p", "port", true, "bind the server to this port." );
		options.addOption( "u", "udp", false, "run the server in UDP mode instead tcp. (def: false)" );
		return options;
	}
	
	public void setNoConfigFileFound() {
		configFileNotFound = true;
	}
}
