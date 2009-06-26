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
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class FrontendTask implements Callable<Integer> {

	private final static int newClientInterests = SelectionKey.OP_READ;
	
	private CommandFactory commandFactory;
	private BackendPoolExecutor backend;
	private Selector serverSelector;		
	private Logger logger = Logger.getLogger(FrontendTask.class);

	private LinkedBlockingQueue<SelectionKey> queue;

	public FrontendTask(CommandFactory cmdFactory, 
			Selector serverSelectorForAccept, BackendPoolExecutor backend,
			LinkedBlockingQueue<SelectionKey> queue) {
		this.queue = queue;
		commandFactory = cmdFactory;
		this.backend = backend;
		serverSelector = serverSelectorForAccept;
	}

	public Integer call() throws Exception {
		while (true) {
			SelectionKey key = queue.take();
			processRequest(key);
		}
	}
	
	protected void processRequest(SelectionKey key) {
		int availOps = key.readyOps();

		logger.debug(String.format("kro=%d",availOps));
		read(key, availOps);
		write(key, availOps);
		accept(key, availOps);
	}
	
	private void read(SelectionKey sk, int availOperations) {
		if(!isOpAvailable(availOperations, SelectionKey.OP_READ))
			return;
		
		EstablishedConnection connHandler = getChannel(sk);
		List<ServerCommand> cmdlist = connHandler.doRead();
		if(null != cmdlist)
			for(ServerCommand cmd : cmdlist)
				backend.offer(cmd);
	}
 
	private void write(SelectionKey sk, int availOperations) {
		if(!isOpAvailable(availOperations, SelectionKey.OP_WRITE))
			return;

		EstablishedConnection connHandler = getChannel(sk);
		connHandler.doWrite();
	}
	
	private void accept(SelectionKey selection, int availOperations){
		if(!isOpAvailable(availOperations, SelectionKey.OP_ACCEPT))
			return;

		accept(selection);
	}
 
	private void accept(SelectionKey selection) {
		ServerSocketChannel listenerSocketChannel = (ServerSocketChannel)selection.channel();
		try {
			SocketChannel clientChannel = forkSocketChannel(listenerSocketChannel);
			Socket clientSocket = getInnerSocketData(clientChannel);
			logger.info("[<=>] " + printRemoteAddress(clientSocket));
			registerNewSocketToSelector(clientChannel, printRemoteAddress(clientSocket));
 		}
		catch(IOException re){logger.error("[<=>] :", re);}
	}

	private void registerNewSocketToSelector(SocketChannel clientChannel, String connectionName) 
	throws ClosedChannelException {
		SelectionKey registeredSelectionKey = clientChannel.register(serverSelector, FrontendTask.newClientInterests, null);
		attachNewChannel(registeredSelectionKey, connectionName);
	}

	private void attachNewChannel(SelectionKey registeredSelectionKey, String connectionName) {
		EstablishedConnection connHandler = new EstablishedConnection(registeredSelectionKey, commandFactory);
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
	
	private EstablishedConnection getChannel(SelectionKey sk){
		return (EstablishedConnection)sk.attachment();
	}
	
	private Socket getInnerSocketData(SocketChannel clientChannel) throws SocketException {
		Socket innerSock = clientChannel.socket();
		innerSock.setKeepAlive(true);
		return innerSock;
	}
	
	private String printRemoteAddress(Socket sock) {
		return sock.getInetAddress()+":"+sock.getPort();
	}
 	
	private boolean isOpAvailable(int availOperations, int reqOp){
		return (availOperations & reqOp) == reqOp;
	}
}
