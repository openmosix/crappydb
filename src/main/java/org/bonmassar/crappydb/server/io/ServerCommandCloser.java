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

public class ServerCommandCloser {

	protected DBConnectionStatus state;
	private Logger logger = Logger.getLogger(ServerCommandCloser.class);
	private SelectionKey selector;

	protected enum DBConnectionStatus{ OPENED, CLOSED; }

	public ServerCommandCloser(SelectionKey selector){
		state = DBConnectionStatus.OPENED;
		this.selector = selector;
	}
	
	public void closeConnection() {
		if(DBConnectionStatus.CLOSED == state)
			return;

		SocketChannel sc = (SocketChannel)selector.channel();
		closeSocketChannel(sc);
		
		state = DBConnectionStatus.CLOSED;
	}

	private void closeSocketChannel(SocketChannel sc) {
		if(!isChannelOpen(sc)) {
			logger.warn("Connection already closed");
			return;
		}

		logger.debug("Closing connection");
		closeChannel(sc);
	}

	private void closeChannel(SocketChannel sc) {
		try {
            closeDescriptor(sc);
        }
		catch(IOException ce){
			logger.error("close failed", ce);
		}
	}

	protected void closeDescriptor(SocketChannel sc) throws IOException {
		sc.close();
		selector.selector().wakeup();
		selector.attach(null);
	}
	
	protected boolean isChannelOpen(SocketChannel sc){
		return null != sc && sc.isOpen();
	}
	
}
