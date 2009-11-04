package org.bonmassar.crappydb.server.io.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.io.ServerCommandWriter;

public class UdpCommandWriter extends ServerCommandWriter {

	private SocketAddress client;
	private DatagramChannel channel;
	private static final Logger logger = Logger.getLogger(UdpCommandWriter.class);
	
	public UdpCommandWriter(SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
	}

	@Override
	public void write() throws IOException {
		if(null == client || null == channel){
			logger.warn("Client or udp channel is null, no response.");
			return;
		}
		
		synchronized(bufferList){
			if(bufferList.isEmpty())
				return;
			
			for (ByteBuffer buffer : bufferList) {
				channel.send(buffer, client);
			}
		}		
	}

	public void setClient(DatagramChannel channel, SocketAddress client) {
		this.client = client;
		this.channel = channel;
	}

	protected void addToQueue(ByteBuffer bb) {
		synchronized(bufferList){
			bufferList.addLast(bb);
		}
	}
}
