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
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class Connection {
		private ConnectionStatus state;
		private ServerCommandReader commandReader;
		private ServerCommandWriter commandWriter;
		private SelectionKey selector;
		private String name;
		
		private Logger logger = Logger.getLogger(Connection.class);
		
		private enum ConnectionStatus{
			OPENED,
			CLOSED;
		}
			        
		public Connection(SelectionKey selector){
			state = ConnectionStatus.OPENED;
			this.selector = selector;
	        selector.attach(this);
						
			commandReader = new ServerCommandReader(selector);
			commandWriter = new ServerCommandWriter(selector);
		}
		
		public ServerCommand doRead()
		{
			try {
				ServerCommand cmd = commandReader.decodeCommand();
				if(null != cmd)
					cmd.attachCommandWriter(commandWriter);
				return cmd;
			} catch (CrappyDBException e) {
				commandWriter.write(e.toString().getBytes());
			} catch (IOException e) {
				logger.error("Error reading remote data", e);
				close();
			}
			return null;
		}
		
		public void close()	{	
			if(state == ConnectionStatus.CLOSED)
				return;

			SocketChannel sc = (SocketChannel)selector.channel();
			if(sc.isOpen()) {
				logger.debug("Closing connection");
				try {
                    sc.close();
                    selector.selector().wakeup();
                    selector.attach(null);
                }
				catch(IOException ce){logger.error("close failed");}
			}
			else logger.warn("Connection already closed");
			state = ConnectionStatus.CLOSED;
		}
	
		public void doWrite() {
			logger.debug("write ready");
			selector.interestOps(SelectionKey.OP_READ);
			try {
				commandWriter.flushOnSocket();
			} catch (IOException e) {
				logger.warn(e);
				close();
			}
		}	
			
		public void setConnectionId(String nm){name = nm;}	
}
