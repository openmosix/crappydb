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

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.io.CrappyNetworkServer;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
 
public class CrappyDBD {

	private final static Logger logger = Logger.getLogger(CrappyDBD.class);
	private static CrappyNetworkServer serverInstance;
	private static ShutdownExecutionRegister threadsKiller;
	private static StorageAccessLayer sal;
	
	public CrappyDBD(String[] args) throws ParseException {
		Configuration.INSTANCE.parse(args);
	}
	
	public void boot() {
		sal = SALFactory.newInstance(Configuration.INSTANCE.getStorage());
		CommandFactory cmdFactory = new CommandFactory(sal);
		(new HomerBoot()).splashScreen();
		
		if(Configuration.INSTANCE.isDumpParams()){
			String[] config = Configuration.INSTANCE.getConfigParams().split("\n");
			logger.info("Dumping config parameters");
			for (int i = 0; i < config.length; i++) {
				logger.info(config[i]);				
			}
		}

		threadsKiller = new ShutdownExecutionRegister();
		Runtime.getRuntime().addShutdownHook(threadsKiller);

		DBStats.INSTANCE.registerThread();
		
		CrappyDBD.serverInstance = new CrappyNetworkServer(cmdFactory, Configuration.INSTANCE.getServerPort()).serverSetup();
		CrappyDBD.serverInstance.start();
	}
	
	static public void main(String [] args){
		CrappyDBD server = null;
		try {
			server = new CrappyDBD(args);
		} catch (ParseException e) {
			System.err.println("Invalid option parameter: "+e.getMessage());
			logger.fatal("Invalid option parameter: "+e.getMessage());
			return;
		}
		
		if(Configuration.INSTANCE.isHelpMessage()){
			Configuration.INSTANCE.generateHelp();
			return;
		}
		
		if(Configuration.INSTANCE.isVersion()){
			System.out.println("CrappyDBD "+DBStats.INSTANCE.getDBVersion());
			return;
		}
				
		server.boot();
	} 
	
	static public void shutdown() {
		threadsKiller.start();
		sal.close();
	}
}