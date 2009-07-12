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
import java.util.List;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

class EstablishedConnection {
		protected ServerCommandReader commandReader;
		protected ServerCommandWriter commandWriter;
		protected ServerCommandCloser commandCloser;
		
		private Logger logger = Logger.getLogger(EstablishedConnection.class);
					        
		public EstablishedConnection(SelectionKey selector, CommandFactory commandFactory){
	        selector.attach(this);
						
			init(selector, commandFactory);
		}
		
		public List<ServerCommand> doRead()
		{
			try {
				List<ServerCommand> cmdlist = commandReader.decodeCommands();
				for(ServerCommand cmd : cmdlist)
					injectWriter(cmd);
				return cmdlist;
			} catch (IOException e) {
				logger.error("Error reading remote data", e);
				commandCloser.closeConnection();
			}
			return null;
		}
			
		public void doWrite() {
			logger.debug("write ready");
			try {
				commandWriter.write();
			} catch (IOException e) {
				logger.warn("Error writing data to remote party", e);
				commandCloser.closeConnection();
			}
		}	
		
		private ServerCommand injectWriter(ServerCommand cmd) {
			if(null != cmd)
				cmd.attachCommandWriter(commandWriter);
			return cmd;
		}
		
		protected void init(SelectionKey selector, CommandFactory commandFactory) {
			commandReader = new ServerCommandReader(selector, commandFactory);
			commandWriter = new ServerCommandWriter(selector);
			commandCloser = new ServerCommandCloser(selector);
		}
			
		public void setConnectionId(String id){
			commandReader.setConnectionId(id);
			commandWriter.setConnectionId(id);
			commandCloser.setConnectionId(id);
		}	
}
