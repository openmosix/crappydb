package org.bonmassar.crappydb.server.io.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.bonmassar.crappydb.server.io.ServerCommandWriter;
import org.bonmassar.crappydb.server.stats.DBStats;

public class TcpCommandWriter extends ServerCommandWriter {

	public TcpCommandWriter(SelectionKey requestsDescriptor) {
		super(requestsDescriptor);
	}

	public void write() throws IOException {
		requestsDescriptor.interestOps(SelectionKey.OP_READ);

		SocketChannel sc = (SocketChannel)requestsDescriptor.channel();
		assertOpenChannel(sc); 
		
		writeToSocketChannel(sc);
	}

	private void writeToSocketChannel(SocketChannel sc) throws IOException {
		synchronized(bufferList){
			if(0 == bufferList.size())
				return;
			
			for(Iterator<ByteBuffer> bit = bufferList.iterator(); bit.hasNext();){
				if(!writeBufferElementToSocketChannel(sc, bit.next()))
					break;
				bit.remove();
			}
		}
	}
	
	private void assertOpenChannel(SocketChannel sc) throws IOException {
		if(null != sc && sc.isOpen())
			return;
		
		logger.warn(String.format("[ =>] [%s] Write closed", connectionid));
		throw new IOException("Channel closed while writing");
	}
	
	private boolean writeBufferElementToSocketChannel(SocketChannel sc, ByteBuffer buffer) throws IOException {
		if(!buffer.hasRemaining())
			return true;
		
		int nbytes = sc.write(buffer);
		if(logger.isDebugEnabled())
			logger.debug(String.format("[ =>] [%s] Sent %d bytes", connectionid, nbytes));
		
		DBStats.INSTANCE.getConnections().newSend(nbytes);
		return continueWritingIfCurrentCompleted(buffer);
	}

	private boolean continueWritingIfCurrentCompleted(ByteBuffer buffer) {
		if(!buffer.hasRemaining()) 
			return true;
		
		if(logger.isDebugEnabled())
			logger.debug(String.format("[ =>] [%s] Write blocked", connectionid));
		
		requestsDescriptor.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		return false;
	}

}
