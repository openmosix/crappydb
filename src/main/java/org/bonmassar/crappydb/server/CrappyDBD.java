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

import org.bonmassar.crappydb.server.io.Connection;
 
public class CrappyDBD {
	
	private final static int newClientInterests = SelectionKey.OP_READ;
	private final static boolean asyncOperations = true;
	
	private ServerSocket listenSock;
	private ServerSocketChannel listenChannel;
	private Selector serverSelector;		
	private int serverPort;
	
	static public void main(String [] args)
	{
		int port = 11211;
		CrappyDBD newinstance = new CrappyDBD(port);
		newinstance.serverSetup();
		newinstance.start();
	} 
	
	public CrappyDBD(int port) {
		super();
		serverPort = port;
	}
	
	public void serverSetup() {
		System.out.println("listening on port="+serverPort);
		try
		{
			initListenChannel();
			initListenSocket();
			registerMainSocketToListener();
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
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

		System.out.println("kro="+availOps);
		read(key, availOps);
		write(key, availOps);
		accept(key, availOps);
	}

	private Iterator<SelectionKey> select() {
		try{
			int pendingio = serverSelector.select();
			System.out.println("select pendingio="+pendingio);
		}
		catch(Exception e){System.err.println("select failed");}
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
			System.out.println("[<=>] " + printRemoteAddress(clientSocket));
			registerNewSocketToSelector(clientChannel, printRemoteAddress(clientSocket));
 		}
		catch(IOException re){System.out.println("[<=>] :");}
	}
	
	private void registerNewSocketToSelector(SocketChannel clientChannel, String connectionName) 
		throws ClosedChannelException {
		SelectionKey registeredSelectionKey = clientChannel.register(serverSelector, CrappyDBD.newClientInterests, null);
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
		socket.configureBlocking(!CrappyDBD.asyncOperations);
	}
	
	private void configureBlocking(ServerSocketChannel socket) throws IOException{
		socket.configureBlocking(!CrappyDBD.asyncOperations);
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