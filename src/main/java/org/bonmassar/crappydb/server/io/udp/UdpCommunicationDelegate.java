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

package org.bonmassar.crappydb.server.io.udp;

import java.nio.channels.SelectionKey;

import org.bonmassar.crappydb.server.io.CommunicationDelegateAbstract;
import org.bonmassar.crappydb.server.io.TransportSession;
import org.bonmassar.crappydb.server.stats.DBStats;

class UdpCommunicationDelegate extends CommunicationDelegateAbstract {

	public TransportSession accept(SelectionKey sk) {
		throw new IllegalStateException("Accept not available in datagram mode.");
	}

	@Override
	public TransportSession read(SelectionKey sk) {
//		logger.info(String.format("[<=>] New connection from %s", printRemoteAddress(clientSocket)));
		DBStats.INSTANCE.getConnections().newConnection();
		TransportSession connHandler = super.read(sk);
		if(null != connHandler)
			connHandler.doWrite();
		DBStats.INSTANCE.getConnections().closeConnection();
		return connHandler;
	}

	public TransportSession write(SelectionKey sk) {
		throw new IllegalStateException("Accept not available in datagram mode.");
	}

	public TransportSession getSession(SelectionKey sk) {
		return new UdpTransportSession(sk);
	}

}
