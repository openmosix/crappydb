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
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class CrappyNetworkServer {
	
	private final static int newClientInterests = SelectionKey.OP_READ;
	private final static boolean asyncOperations = true;
	
	private ServerSocket listenSock;
	private ServerSocketChannel listenChannel;
	private Selector serverSelector;		
	private int serverPort;
	
	private Logger logger = Logger.getLogger(CrappyNetworkServer.class);
	
	public CrappyNetworkServer(int port) {
		super();
		serverPort = port;
	}
	
	public void serverSetup() {
		logger.info(String.format("listening on port %d", serverPort));
		try
		{
			initListenChannel();
			initListenSocket();
			registerMainSocketToListener();
			logger.info(String.format("Server up!"));
		}
		catch(IOException ie)
		{
			logger.fatal("Cannot init the network server", ie);
			System.exit(0);
		}
	}

	public void start()
	{  
		while(true)
			processRequests();
	}

	private void processRequests() {
		Iterator<SelectionKey> pendingRequests = select();
		while(pendingRequests.hasNext())
		{
			SelectionKey key = pendingRequests.next();
			processRequest(key);
			pendingRequests.remove();
		}
	}

	private void processRequest(SelectionKey key) {
		int availOps = key.readyOps();

		logger.debug(String.format("kro=%d",availOps));
		read(key, availOps);
		write(key, availOps);
		accept(key, availOps);
	}

	private Iterator<SelectionKey> select() {
		try{
			int pendingio = serverSelector.select();
			logger.debug(String.format("select pendingio %d"+pendingio));
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
	
	private void accept(SelectionKey selection, int availOperations){
		if(!isOpAvailable(availOperations, SelectionKey.OP_ACCEPT))
			return;

		accept(selection);
	}
 
	private void accept(SelectionKey selection)
	{
		ServerSocketChannel listenerSocketChannel = (ServerSocketChannel)selection.channel();
		try
		{
			SocketChannel clientChannel = forkSocketChannel(listenerSocketChannel);
			Socket clientSocket = getInnerSocketData(clientChannel);
			logger.info("[<=>] " + printRemoteAddress(clientSocket));
			registerNewSocketToSelector(clientChannel, printRemoteAddress(clientSocket));
 		}
		catch(IOException re){logger.error("[<=>] :", re);}
	}
	
	private void registerNewSocketToSelector(SocketChannel clientChannel, String connectionName) 
		throws ClosedChannelException {
		SelectionKey registeredSelectionKey = clientChannel.register(serverSelector, CrappyNetworkServer.newClientInterests, null);
		attachNewChannel(registeredSelectionKey, connectionName);
	}

	private void attachNewChannel(SelectionKey registeredSelectionKey, String connectionName) {
		Connection connHandler = new Connection(registeredSelectionKey);
		connHandler.setConnectionId(connectionName);
		registeredSelectionKey.attach(connHandler);
	}

	private SocketChannel forkSocketChannel(ServerSocketChannel sc) throws IOException{
		SocketChannel clientSocket = sc.accept();
		configureBlocking(clientSocket);
		return clientSocket;
	}
	
	private void configureBlocking(SocketChannel socket) throws IOException{
		socket.configureBlocking(!CrappyNetworkServer.asyncOperations);
	}
	
	private void configureBlocking(ServerSocketChannel socket) throws IOException{
		socket.configureBlocking(!CrappyNetworkServer.asyncOperations);
	}
	
	private Socket getInnerSocketData(SocketChannel clientChannel) throws SocketException {
		Socket innerSock = clientChannel.socket();
		innerSock.setKeepAlive(true);
		return innerSock;
	}
	
	private String printRemoteAddress(Socket sock) {
		return sock.getInetAddress()+":"+sock.getPort();
	}
 
	private void read(SelectionKey sk, int availOperations) {
		if(!isOpAvailable(availOperations, SelectionKey.OP_READ))
			return;
		
		Connection connHandler = getChannel(sk);
		connHandler.doRead();
	}
 
	private void write(SelectionKey sk, int availOperations) {
		if(!isOpAvailable(availOperations, SelectionKey.OP_WRITE))
			return;

		Connection connHandler = getChannel(sk);
		connHandler.doWrite();
	}
	
	private boolean isOpAvailable(int availOperations, int reqOp){
		return (availOperations & reqOp) == reqOp;
	}
	
	private Connection getChannel(SelectionKey sk){
		return (Connection)sk.attachment();
	}
}