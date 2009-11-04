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

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.ClosedConnectionException;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class CommunicationTask implements Callable<Void> {
	
	public abstract static class CommunicationDelegate {
		abstract public TransportSession accept(SelectionKey sk);
		abstract public TransportSession write(SelectionKey sk);
		
		public TransportSession read(SelectionKey sk){
			TransportSession connHandler = getSession(sk);

			List<ServerCommand> cmdlist = connHandler.doRead();
			if(null == cmdlist)
				return null;
			
			for(ServerCommand cmd : cmdlist)
				try {
					cmd.execCommand();
				} catch (ClosedConnectionException e) {
					connHandler.doClose();
					break;
				}
			
			return connHandler;
		}
		
		abstract public TransportSession getSession(SelectionKey sk);
	}
	
	private final TransportProtocol tcp;
	private final TransportProtocol udp;
	protected SelectionKey key;
	private Logger logger = Logger.getLogger(CommunicationTask.class);

	public CommunicationTask(TransportProtocol tcp, TransportProtocol udp, SelectionKey key) {
		this.tcp = tcp;
		this.udp = udp;
		this.key = key;
	}

	public Void call() throws Exception {
		try{
			processRequest(key);
		}catch(java.lang.Throwable t){
			logger.fatal("Internal error executing db operations", t);
		}
		return null;
	}
		
	private void processRequest(SelectionKey key) {
		int availOps = key.readyOps();

		if(logger.isDebugEnabled())
			logger.debug(String.format("IO ready for %s => %s", key, selectResultToString(availOps)));
		
		Channel ch = (Channel) key.channel();
		
		accept(ch, key, availOps);
		read(ch, key, availOps);
		write(ch, key, availOps);		
	}
	
	private void accept(Channel ch, SelectionKey sk, int availOperations){
		if(!sk.isAcceptable())
			return;
		
		if(tcp.isValidChannel(ch)) 
			tcp.comms().accept(key);
	}
	
	private void read(Channel ch, SelectionKey sk, int availOperations) {
		if(!sk.isReadable())
			return;
		
		if(udp.isValidChannel(ch)) 
			udp.comms().read(key);
		else
			tcp.comms().read(key);
	}
 
	private void write(Channel ch, SelectionKey sk, int availOperations) {
		if(!sk.isWritable())
			return;
		
		if(udp.isValidChannel(ch)) 
			udp.comms().write(key);
		else
			tcp.comms().write(key);
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
}
