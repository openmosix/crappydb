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

public interface ConfigurationIface {
	
	Catalogue getStorage() throws ParseException;
	
	String getDBPath() throws ParseException;
	
	int getServerPort() throws ParseException ;

	int getBufferSize() throws ParseException ;
	
	int getEngineThreads() throws ParseException ;
	
	boolean isHelpMessage() throws ParseException ;
	
	boolean isDumpParams() throws ParseException ;
	
	boolean isVersion() throws ParseException ;
	
	boolean isUdp() throws ParseException ;
	
	String getHostname() throws ParseException ;
	
	int getMaxPayloadSize()throws ParseException ;
	
	String getConfigurationFileName()throws ParseException ;
}
