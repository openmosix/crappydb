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

package org.bonmassar.crappydb.server.io.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.io.TransportSession;
import org.bonmassar.crappydb.server.io.CommunicationTask;
import org.bonmassar.crappydb.server.stats.DBStats;

class TcpAccept {

	private final static int newClientInterests = SelectionKey.OP_READ;
	private final static Logger logger = Logger.getLogger(CommunicationTask.class);
		
	public void doAccept(SelectionKey selection) {
		ServerSocketChannel listenerSocketChannel = (ServerSocketChannel)selection.channel();
		if(null == listenerSocketChannel)
			return;
		
		try {
			SocketChannel clientChannel = forkSocketChannel(listenerSocketChannel);
			Socket clientSocket = getInnerSocketData(clientChannel);
			logger.info(String.format("[<=>] New connection from %s", printRemoteAddress(clientSocket)));
			registerNewSocketToSelector(selection.selector(), clientChannel, printRemoteAddress(clientSocket));
			DBStats.INSTANCE.getConnections().newConnection();
 		}
		catch(IOException re){logger.error("[<=>] Failure establish connection with a client", re);}
	}
	
	private SocketChannel forkSocketChannel(ServerSocketChannel sc) throws IOException{
		SocketChannel clientSocket = sc.accept();
		configureBlocking(clientSocket);
		return clientSocket;
	}

	private void configureBlocking(SocketChannel socket) throws IOException{
		socket.configureBlocking(false);
	}

	protected void registerNewSocketToSelector(Selector serverSelector, SocketChannel clientChannel, String connectionName) 
		throws ClosedChannelException {
		SelectionKey registeredSelectionKey = clientChannel.register(serverSelector, TcpAccept.newClientInterests, null);
		attachNewChannel(registeredSelectionKey, connectionName);
	}

	protected void attachNewChannel(SelectionKey registeredSelectionKey, String connectionName) {
		TransportSession connHandler = new TcpTransportSession(registeredSelectionKey);
		registeredSelectionKey.attach(connHandler);
		connHandler.setConnectionId(connectionName);
	}
		
	private Socket getInnerSocketData(SocketChannel clientChannel) throws SocketException {
		Socket innerSock = clientChannel.socket();
		innerSock.setKeepAlive(true);
		return innerSock;
	}
	
	private String printRemoteAddress(Socket sock) {
		return sock.getInetAddress()+":"+sock.getPort();
	}
}
