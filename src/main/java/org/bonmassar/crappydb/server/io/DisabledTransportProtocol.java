package org.bonmassar.crappydb.server.io;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Selector;

import org.bonmassar.crappydb.server.io.CommunicationTask.CommunicationDelegate;

public class DisabledTransportProtocol implements TransportProtocol {

	public CommunicationDelegate comms() {
		// TODO Auto-generated method stub
		return null;
	}

	public Selector registerListener() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "";
	}

	public void register(Selector selector) throws IOException {
		// TODO Auto-generated method stub
	}

	public boolean isValidChannel(Channel ch) {
		return false;
	}
}
