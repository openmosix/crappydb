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

import org.bonmassar.crappydb.server.io.CrappyNetworkServer;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.SALFactory.Catalogue;
 
public class CrappyDBD {

	private static final int serverPort = 11211;
	private static CrappyNetworkServer serverInstance;
	private static ShutdownExecutionRegister threadsKiller;
	
	static public void main(String [] args)
	{
		StorageAccessLayer sal = SALFactory.newInstance(Catalogue.INMEMORY_UNBOUNDED_FIXED_RATE_GC);
		CommandFactory cmdFactory = new CommandFactory(sal);
		(new HomerBoot()).splashScreen();

		threadsKiller = new ShutdownExecutionRegister();
		Runtime.getRuntime().addShutdownHook(threadsKiller);

		DBStats.INSTANCE.registerThread();
		
		CrappyDBD.serverInstance = new CrappyNetworkServer(cmdFactory, serverPort).serverSetup();
		CrappyDBD.serverInstance.start();
	} 
	
	static public void shutdown() {
		threadsKiller.start();
	}
}