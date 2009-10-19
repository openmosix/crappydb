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

package org.bonmassar.crappydb.server.io;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;

public class CrappyNetworkServer {
		
	private Logger logger = Logger.getLogger(CrappyNetworkServer.class);

	private TransportProtocol protocol;
	protected Selector serverSelector;		
	private CommandFactory cmdFactory;
	protected DBPoolThreadExecutor frontend;

	public CrappyNetworkServer(CommandFactory cmdFactory) {
		super();
		this.cmdFactory = cmdFactory;
	}

	public CrappyNetworkServer serverSetup() {
		logger.info(String.format("Listening on port %s:%d", printProtocol(), Configuration.INSTANCE.getServerPort()));
		try {
			initProtocol();
			registerMainSocketToListener();
			
			DBPoolThreadExecutor.setup(cmdFactory, serverSelector);
			frontend = new DBPoolThreadExecutor();
			logger.info(String.format("Server up!"));
		}
		catch(IOException ie) {
			logger.fatal("Cannot init the network server", ie);
			throw new RuntimeException("Failed starting daemon - see logs");
		}
		return this;
	}

	private String printProtocol() {
		return Configuration.INSTANCE.isUdp() ? "udp" : "tcp";
	}

	private void initProtocol() throws IOException {
		if(Configuration.INSTANCE.isUdp())
			protocol = new UdpProtocol();
		else 
			protocol = new TcpProtocol();
	}

	public void start() {  
		while(true){
			try {
				processRequests();
			} catch (InterruptedException e) {
				logger.info("Exiting...");
				break;
			}
		}
	}

	protected int processRequests() throws InterruptedException {
		Set<SelectionKey> pendingRequests = select();
		if(null==pendingRequests)
			return 0;
		
		int taskCounter = pendingRequests.size();
		List<Future<Void>> result = new ArrayList<Future<Void>>();
		for(Iterator<SelectionKey> it = pendingRequests.iterator(); it.hasNext();) {
			SelectionKey key = it.next();
			result.add(frontend.submit(key));
			it.remove();
		}
		for(Future<Void> f : result)
			try {
				f.get();
			} catch (ExecutionException e) {
				logger.fatal("Exception executing a subtask", e);
			}
		return taskCounter;
	}

	private Set<SelectionKey> select() {
		try{
			int pendingio = serverSelector.select();
			if(logger.isDebugEnabled())
				logger.debug(String.format("%d IO operation(s) ready to dispatch", pendingio));
		}
		catch(Exception e){
			logger.error("Select IO failed", e);
			return null;
		}
		return serverSelector.selectedKeys();
	}

	private void registerMainSocketToListener() throws IOException,
			ClosedChannelException {
		serverSelector = Selector.open();
		protocol.register(serverSelector);
	}		
}
