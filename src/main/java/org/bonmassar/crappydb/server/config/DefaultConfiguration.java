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

abstract class DefaultConfiguration implements ConfigurationIface {

	protected final static String DB = "dbpath";
	protected final static String STORAGE = "storage";
	protected final static String THREADS = "threads";
	protected final static String DUMP = "dump";
	protected final static String BUFFSIZE = "buffer-size";
	protected final static String MAXPAYLOADSIZE = "max-payload-size";
	protected final static String HOSTNAME = "hostname";
	protected final static String PORT = "port";
	protected final static String VERSION = "version";
	protected final static String HELP = "help";
	protected final static String FILE = "file";
	
	public Catalogue getStorage() throws ParseException{
		return Catalogue.INMEMORY_UNBOUNDED_FIXED_RATE_GC;
	}
	
	public String getDBPath() throws ParseException{
		return "/var/crappydb/db";
	}
	
	public int getBufferSize() throws ParseException {
		return 8 * 1024;
	}

	public int getEngineThreads() throws ParseException {
		return Runtime.getRuntime().availableProcessors() * 2;
	}

	public String getHostname() {
		return null;
	}

	public int getMaxPayloadSize() throws ParseException {
		return 64*1024*1024;
	}

	public int getServerPort() throws ParseException {
		return 11211;
	}
	
	public String getConfigurationFileName() {
		return "crappydb.conf";
	}
	
	protected Catalogue fromCatalogue(String value, String paramName) throws ParseException{
		Catalogue c = Catalogue.valueOf(value);
		if(null == c)
			throw new ParseException(String.format("Invalid value %s for parameter %s. Allowed values are %s.", value, paramName, getStorageAllowedValues()));
		return c;
	}
	
	protected int toInt(String value, String paramName) throws ParseException{
		try{
			return Integer.parseInt(value);
		}catch(NumberFormatException err){
			throw new ParseException(String.format("Invalid value for parameter %s", paramName));
		}
	}
	
	protected String getStorageAllowedValues() {
		StringBuilder sb = new StringBuilder();
		for(Catalogue c : Catalogue.values()){
			if(sb.length() > 0)
				sb.append(", ");
			sb.append(String.format("\"%s\"", c));
		}
		return sb.toString();
	}

	
	protected boolean toBool(String value, String paramName){		
		return Boolean.parseBoolean(value);
	}

}
