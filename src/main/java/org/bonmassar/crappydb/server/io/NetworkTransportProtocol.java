package org.bonmassar.crappydb.server.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.spi.AbstractSelectableChannel;

import org.bonmassar.crappydb.server.config.Configuration;

public abstract class NetworkTransportProtocol implements TransportProtocol {

	protected final AbstractSelectableChannel listenChannel;

	protected NetworkTransportProtocol(AbstractSelectableChannel channel)
	throws IOException {
		if(null == channel)
			throw new NullPointerException("Null channel");

		listenChannel = channel;
		listenChannel.configureBlocking(false);
	}

	protected InetSocketAddress getSocketAddress() {
		if(null == Configuration.INSTANCE.getHostname())
			return new InetSocketAddress(Configuration.INSTANCE.getServerPort());

		return new InetSocketAddress(Configuration.INSTANCE.getHostname(), Configuration.INSTANCE.getServerPort());
	}

	public boolean isValidChannel(Channel ch) {
		return listenChannel == ch;
	}
}
