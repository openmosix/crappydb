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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;

import org.bonmassar.crappydb.server.config.Configuration;

abstract class TransportProtocol {
		
		protected final static boolean asyncOperations = true;

		protected final AbstractSelectableChannel listenChannel;
		
		TransportProtocol(AbstractSelectableChannel channel) throws IOException{
			if(null == channel)
				throw new NullPointerException("Null channel");
			
			listenChannel = channel;
			listenChannel.configureBlocking(!TransportProtocol.asyncOperations);
		}
				
		public void register(Selector selector) throws ClosedChannelException {
			listenChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
		
		protected InetSocketAddress getSocketAddress() {
			if(null == Configuration.INSTANCE.getHostname())
				return new InetSocketAddress(Configuration.INSTANCE.getServerPort());
			
			return new InetSocketAddress(Configuration.INSTANCE.getHostname(), Configuration.INSTANCE.getServerPort());
		}

	}