package org.bonmassar.crappydb.server.io.tcp;

import java.nio.channels.SelectionKey;

import org.bonmassar.crappydb.server.io.TransportSession;
import org.bonmassar.crappydb.server.io.CommunicationTask.CommunicationDelegate;

class TcpCommunicationDelegate extends CommunicationDelegate {
	
	public TransportSession accept(SelectionKey sk) {
		new TcpAccept().doAccept(sk);
		return null;
	}

	public TransportSession write(SelectionKey sk) {
		TransportSession connHandler = getSession(sk);
		connHandler.doWrite();
		return connHandler;
	}
	
	public TransportSession getSession(SelectionKey sk) {
		return (TransportSession)sk.attachment();
	}

}
