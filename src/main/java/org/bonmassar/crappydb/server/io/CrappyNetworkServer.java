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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;

public class CrappyNetworkServer {
	
	protected final static boolean asyncOperations = true;

	private Logger logger = Logger.getLogger(CrappyNetworkServer.class);

	private ServerSocket listenSock;
	private ServerSocketChannel listenChannel;
	protected Selector serverSelector;		
	private int serverPort;
	protected FrontendPoolExecutor frontend;

	public CrappyNetworkServer(CommandFactory cmdFactory, int port) {
		super();
		serverPort = port;
		FrontendPoolExecutor.setup(cmdFactory, serverSelector, new BackendPoolExecutor());
		frontend = new FrontendPoolExecutor();
	}

	public void serverSetup() {
		logger.info(String.format("listening on port %d", serverPort));
		try {
			initListenChannel();
			initListenSocket();
			registerMainSocketToListener();
			logger.info(String.format("Server up!"));
		}
		catch(IOException ie) {
			logger.fatal("Cannot init the network server", ie);
			throw new RuntimeException("Failed starting daemon - see logs");
		}
	}

	public void start() {  
		while(true)
			processRequests();
	}

	protected void processRequests() {
		Iterator<SelectionKey> pendingRequests = select();
		while(pendingRequests.hasNext()) {
			SelectionKey key = pendingRequests.next();
			frontend.offer(key);
			pendingRequests.remove();
		}
	}

	private Iterator<SelectionKey> select() {
		try{
			int pendingio = serverSelector.select();
			logger.debug(String.format("select pending io %d"+pendingio));
		}
		catch(Exception e){logger.error("select failed");}
		return serverSelector.selectedKeys().iterator();
	}

	private void registerMainSocketToListener() throws IOException,
			ClosedChannelException {
		serverSelector = Selector.open();
		listenChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
	}

	private void initListenSocket() throws IOException {
		listenSock = listenChannel.socket();
		listenSock.bind(new InetSocketAddress(serverPort));
	}

	private void initListenChannel() throws IOException {
		listenChannel = ServerSocketChannel.open();
		configureBlocking(listenChannel);
	}
		
	private void configureBlocking(ServerSocketChannel socket) throws IOException{
		socket.configureBlocking(!CrappyNetworkServer.asyncOperations);
	}
		
}
