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
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public enum Configuration {
	INSTANCE;
	
	private CommandLine cli;
	
	private int serverPort;
	private String serverHostname;
	private int engineThreads;
	private int buffSize;
	private int maxPayloadSize;
	private boolean help;
	private boolean version;
	private boolean dump;
	
	private Configuration() {
		fillDefaults();
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public int getBufferSize() {
		return buffSize;
	}
	
	public int getEngineThreads() {
		return engineThreads;
	}
	
	public boolean isHelpMessage() {
		return help;
	}
	
	public boolean isDumpParams() {
		return dump;
	}
	
	public boolean isVersion() {
		return version;
	}
	
	public String getHostname() {
		return serverHostname;
	}
	
	public int getMaxPayloadSize() {
		return maxPayloadSize;
	}
	
	public String getConfigParams() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("dump %s\n", value(isDumpParams())));
		sb.append(String.format("help %s\n", value(isHelpMessage())));
		sb.append(String.format("version %s\n", value(isVersion())));
		sb.append(String.format("hostname %s\n", getHostname() == null ? "*" : getHostname()));
		sb.append(String.format("port %d\n", getServerPort()));
		sb.append(String.format("threads %d\n", getEngineThreads()));
		sb.append(String.format("buffer-size %d\n", getBufferSize()));
		sb.append(String.format("max-payload-size %d\n", getMaxPayloadSize()));
		return sb.toString();
	}

	void parse(String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		
		cli = parser.parse( buildOptions(), args );
		fillDefaults();
		fillUserSettings(cli);
	}
	
	void generateHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "crappydbd", buildOptions() );
	}

	private void fillUserSettings(CommandLine line) throws ParseException {
		fillHelp(line);
		fillServerPortOption(line);
		fillVersion(line);
		fillDump(line);
		fillNumThreads(line);
		fillHostname(line);
		fillBuffSize(line);
		fillMaxPayloadSize(line);
	}

	private void fillHelp(CommandLine line) {
	    if( !line.hasOption( "help" ) )
	    	return;

	    help = true;
	}
	
	private void fillDump(CommandLine line) {
	    if( !line.hasOption( "dump" ) )
	    	return;

	    dump = true;
	}
	
	private void fillHostname(CommandLine line) {
	    if( !line.hasOption( "hostname" ) )
	    	return;

	    serverHostname = line.getOptionValue( "hostname" ).trim();
	}
	
	private void fillVersion(CommandLine line) {
	    if( !line.hasOption( "version" ) )
	    	return;

	    version = true;
	}
	
	private void fillMaxPayloadSize(CommandLine line) throws ParseException{
	    if( !line.hasOption( "max-payload-size" ) )
	    	return;
	    
	    try{
	    	maxPayloadSize =  Integer.valueOf(line.getOptionValue( "max-payload-size" ).trim());
	    }catch(NumberFormatException nfe){
	    	throw new ParseException("Invalid format for threads parameter.");
	    }
	}
	
	private void fillNumThreads(CommandLine line) throws ParseException{
	    if( !line.hasOption( "threads" ) )
	    	return;
	    
	    try{
	    	engineThreads =  Integer.valueOf(line.getOptionValue( "threads" ).trim());
	    }catch(NumberFormatException nfe){
	    	throw new ParseException("Invalid format for threads parameter.");
	    }
	}
	
	private void fillBuffSize(CommandLine line) throws ParseException{
	    if( !line.hasOption( "buffer-size" ) )
	    	return;
	    
	    try{
	    	buffSize =  Integer.valueOf(line.getOptionValue( "buffer-size" ).trim());
	    }catch(NumberFormatException nfe){
	    	throw new ParseException("Invalid format for buffer-size parameter.");
	    }

	}

	private void fillDefaults() {
		serverPort = 11211;
		buffSize = 8 * 1024;
		maxPayloadSize = 64*1024*1024;	//64Mb
		serverHostname = null;
		help = false;
		version = false;
		dump = false;
		engineThreads = Runtime.getRuntime().availableProcessors() * 2;
	}
	
	private Options buildOptions() {
		Options options = new Options();
		helpOption(options);
		versionOption(options);
		serverPortOption(options);
		dumpOption(options);
		engineThreadsOption(options);
		serverHostnameOption(options);
		bufferSizeOption(options);
		payloadSizeOption(options);
		return options;
	}
	
	private void bufferSizeOption(Options options) {
		options.addOption( null, "buffer-size", true, "define the internal buffer size for IO operations. (def: 8K)" );
	}
	
	private void payloadSizeOption(Options options) {
		options.addOption( null, "max-payload-size", true, "define the maximum payload size for a write operation (def: 64M)" );
	}
	
	private void engineThreadsOption(Options options) {
		options.addOption( "t", "threads", true, "number of engine threads (def: number of processors x 2)." );
	}
	
	private void serverHostnameOption(Options options) {
		options.addOption( "h", "hostname", true, "bind the server to this hostname." );
	}
	
	private void serverPortOption(Options options) {
		options.addOption( "p", "port", true, "bind the server to this port." );
	}
	
	private void helpOption(Options options) {
		options.addOption( null, "help", false, "print this help message." );
	}
	
	private void versionOption(Options options) {
		options.addOption( "v", "version", false, "print server version." );
	}
	
	private void dumpOption(Options options) {
		options.addOption( null, "dump", false, "dump runtime config parameters in log files." );
	}
	
	private void fillServerPortOption(CommandLine line) throws ParseException {
	    if( !line.hasOption( "port" ) )
	    	return;

	    try{
	    	serverPort =  Integer.valueOf(line.getOptionValue( "port" ).trim());
	    }catch(NumberFormatException nfe){
	    	throw new ParseException("Invalid format for port parameter.");
	    }
	}
	
	private String value(boolean value) {
		return (value)?"on":"off";
	}

}
