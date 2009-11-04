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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import org.bonmassar.crappydb.server.io.NetworkTransportProtocol;
import org.bonmassar.crappydb.server.io.CommunicationTask.CommunicationDelegate;

public class TcpProtocol extends NetworkTransportProtocol {
	
	private final TcpCommunicationDelegate delegate;
	
	public TcpProtocol() throws IOException{
		super(ServerSocketChannel.open());

		((ServerSocketChannel) listenChannel).socket().bind(getSocketAddress());
		delegate = new TcpCommunicationDelegate();
	}

	public void register(Selector selector) throws ClosedChannelException {
		listenChannel.register(selector, SelectionKey.OP_ACCEPT);		
	}

	@Override
	public String toString() {
		return "tcp";
	}

	public CommunicationDelegate comms() {
		return delegate;
	}
}