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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.ParseException;

class FileConfiguration extends CLIConfiguration{

	private final Properties properties;
		
	public FileConfiguration(String filename) throws FileNotFoundException, IOException {
		properties = new Properties();
		properties.load(new FileInputStream(filename));
	}
		
	@Override
	public int getBufferSize() throws ParseException {
		String bufsize = properties.getProperty(BUFFSIZE);
		if(null == bufsize)
			return super.getBufferSize();
		
		return toInt(bufsize, BUFFSIZE);
	}

	@Override
	public int getEngineThreads() throws ParseException {
		String threads = properties.getProperty(THREADS);
		if(null == threads)
			return super.getEngineThreads();
		
		return toInt(threads, THREADS);
	}

	@Override
	public String getHostname() {
		String hostname = properties.getProperty(HOSTNAME);
		if(null == hostname)
			return super.getHostname();
		
		return hostname;
	}

	@Override
	public int getMaxPayloadSize() throws ParseException {
		String payloadSize = properties.getProperty(MAXPAYLOADSIZE);
		if(null == payloadSize)
			return super.getMaxPayloadSize();
		
		return toInt(payloadSize, MAXPAYLOADSIZE);
	}

	@Override
	public int getServerPort() throws ParseException {
		String port = properties.getProperty(PORT);
		if(null == port)
			return super.getServerPort();
		
		return toInt(port, PORT);
	}

	@Override
	public boolean isDumpParams() {
		String dump = properties.getProperty(DUMP);
		if(null == dump)
			return super.isDumpParams();
		
		return toBool(dump, DUMP);
	}
	
	@Override
	public boolean isUdp() {
		String udp = properties.getProperty(UDP);
		if(null == udp)
			return super.isUdp();
		
		return toBool(udp, UDP);
	}
}