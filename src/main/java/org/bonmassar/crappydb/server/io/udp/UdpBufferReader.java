package org.bonmassar.crappydb.server.io.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;

import org.bonmassar.crappydb.server.io.BufferReader;

public class UdpBufferReader extends BufferReader {

	private static int maxUDPPacketSize = 65507;
	private final UdpCommandWriter writer;
	
	public UdpBufferReader(UdpCommandWriter writer, SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
		this.writer = writer;
		buffer = ByteBuffer.allocateDirect(maxUDPPacketSize);
	}

	@Override
	protected int channelRead(ReadableByteChannel channel) throws IOException {
		if(!(channel instanceof DatagramChannel))
			throw new IllegalArgumentException("Channel is not a datagram channel");
		
		DatagramChannel UDPChannel = ((DatagramChannel)channel);
		SocketAddress client = UDPChannel.receive(buffer);
		writer.setClient(UDPChannel, client);
		return buffer.limit();
	}

}
