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

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

class FrontendTask implements Callable<Integer> {
	
	private FrontendPoolExecutor frontend;
	protected ServerCommandAccepter accepter;
	private Logger logger = Logger.getLogger(FrontendTask.class);


	public FrontendTask(CommandFactory cmdFactory, 
			Selector parentSelector, 
			FrontendPoolExecutor frontend) {
		this.frontend = frontend;
		this.accepter = new ServerCommandAccepter(cmdFactory, parentSelector);
	}

	public Integer call() throws Exception {
		while (true) 
			executeTask();
	}
	
	public void executeTask() throws InterruptedException {
		SelectionKey key = frontend.take();
		processRequest(key);
		frontend.getBarrier().countDown();
	}
	
	private void processRequest(SelectionKey key) {
		int availOps = key.readyOps();

		if(logger.isDebugEnabled())
			logger.debug(String.format("IO ready for %s => %s", key, selectResultToString(availOps)));
		
		read(key, availOps);
		write(key, availOps);
		accept(key, availOps);
	}
	
	private String selectResultToString(int pendingio){
		StringBuilder sb = new StringBuilder();
		if((pendingio & SelectionKey.OP_ACCEPT)==SelectionKey.OP_ACCEPT)
			sb.append("ACCEPT-");
		if((pendingio & SelectionKey.OP_CONNECT)==SelectionKey.OP_CONNECT)
			sb.append("-CONNECT-");
		if((pendingio & SelectionKey.OP_READ)==SelectionKey.OP_READ)
			sb.append("-READ-");
		if((pendingio & SelectionKey.OP_WRITE)==SelectionKey.OP_WRITE)
			sb.append("-WRITE");
		return sb.toString();
	}
	
	private void read(SelectionKey sk, int availOperations) {
		if(!isOpAvailable(availOperations, SelectionKey.OP_READ))
			return;
		
		EstablishedConnection connHandler = getChannel(sk);

		List<ServerCommand> cmdlist = connHandler.doRead();
		if(null != cmdlist)
			for(ServerCommand cmd : cmdlist)
				cmd.execCommand();
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

		accepter.doAccept(selection);
	}
 
	protected EstablishedConnection getChannel(SelectionKey sk){
		return (EstablishedConnection)sk.attachment();
	}
 	
	private boolean isOpAvailable(int availOperations, int reqOp){
		return (availOperations & reqOp) == reqOp;
	}
}
