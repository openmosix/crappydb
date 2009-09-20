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

package org.bonmassar.crappydb.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public enum Configuration {
	INSTANCE;
	
	private long serverPort;
	
	public long getServerPort() {
		return serverPort;
	}
	
	void parse(String[] args) {
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption(buildServerPortOption());
		
		CommandLine line;
		try {
			line = parser.parse( options, args );
			
		    // validate that block-size has been set
		    if( line.hasOption( "port" ) ) {
		        // print the value of block-size
		    	serverPort =  Long.valueOf(line.getOptionValue( "port" ));
		    }
		    else
		    	serverPort = 11211;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Option buildServerPortOption() {
		return OptionBuilder.withLongOpt("port").hasOptionalArg()
			.withDescription("Listen port").create("port");
	}
}
