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

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.server.storage.SALFactory.Catalogue;

public enum Configuration {
	INSTANCE;
		
	private CLIConfiguration config;
	
	private Catalogue storage;
	private String dbpath;
	private int serverPort;
	private String serverHostname;
	private int engineThreads;
	private int buffSize;
	private int maxPayloadSize;
	private boolean help;
	private boolean version;
	private boolean dump;
	private boolean udp;
	private String configFile;
	
	private String configFileName() {
		return configFile;
	}
	
	public Catalogue getStorage() {
		return storage;
	}
	
	public String getDbPath() {
		return dbpath;
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
	
	public boolean isUdp() {
		return udp;
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
	
	private void loadConfiguration(CLIConfiguration config) throws ParseException {
		serverPort = config.getServerPort();
		serverHostname = config.getHostname();
		engineThreads = config.getEngineThreads();
		buffSize = config.getBufferSize();
		maxPayloadSize = config.getMaxPayloadSize();
		help = config.isHelpMessage();
		version = config.isVersion();
		dump = config.isDumpParams();
		configFile = config.getConfigurationFileName();
		storage = config.getStorage();
		dbpath = config.getDBPath();
		udp = config.isUdp();
	}
	
	public String getConfigParams() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("file %s\n", configFileName()));
		sb.append(String.format("dump %s\n", value(isDumpParams())));
		sb.append(String.format("help %s\n", value(isHelpMessage())));
		sb.append(String.format("version %s\n", value(isVersion())));
		sb.append(String.format("hostname %s\n", getHostname() == null ? "*" : getHostname()));
		sb.append(String.format("port %d\n", getServerPort()));
		sb.append(String.format("threads %d\n", getEngineThreads()));
		sb.append(String.format("buffer-size %d\n", getBufferSize()));
		sb.append(String.format("max-payload-size %d\n", getMaxPayloadSize()));
		sb.append(String.format("storage %s\n", getStorage()));		
		sb.append(String.format("dbpath %s\n", getDbPath()));
		sb.append(String.format("udp %s\n", value(isUdp())));
		return sb.toString();
	}
	
	private String value(boolean value) {
		return (value)?"on":"off";
	}
	
	public void parse(String[] args) throws ParseException {
		config = new ConfigurationBuilder().getConfig(args);
		loadConfiguration(config);
	}

	public void generateHelp() {
		config.generateHelp();
	}

}
