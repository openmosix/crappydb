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

package org.bonmassar.crappydb.server.memcache.protocol;

import org.bonmassar.crappydb.server.config.Configuration;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.utils.Base64;

abstract class ServerCommandWithPayload extends ServerCommandAbstract {

	protected static final int KEY_POS=0;
	protected static final int FLAGS_POS=1;
	protected static final int EXPTIME_POS=2;
	protected static final int BYTES_POS=3;
	protected static final int NOREPLY_POS=4;
	protected static final int CRLF=2;
	
	protected int minparams = 4;
	protected int maxparams = 5;
	
	byte[] payload;
	int payloadCursor;

	@Override
	protected int getNoReplyPosition() {
		return NOREPLY_POS;
	}
	
	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		super.parseCommandParams(commandParams);
		if(params.length < minparams || params.length > maxparams)
			throw new ErrorException("Invalid number of parameters");
		
		initPayload();
	}

	public int payloadContentLength() {
		try{
			return Integer.parseInt(params[SetServerCommand.BYTES_POS])+CRLF;
		}catch(NumberFormatException nfe){
			return 0;
		}
	}
	
	public void addPayloadContentPart(byte[] data) {
		if(null == data || data.length == 0 || payload.length == payloadCursor)
			return;
		
		int length = (data.length + payloadCursor > payload.length) ? 
				(payload.length - payloadCursor) : data.length;
		
		System.arraycopy(data, 0, payload, payloadCursor, length);
		payloadCursor += length;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format("{%s key=%s flags=%s expire=%s nbytes=%s noreply=%s%s}", 
				getCommandName(), params[KEY_POS],
				params[FLAGS_POS], params[EXPTIME_POS],params[BYTES_POS],
				!isResponseRequested()?"true":"false", 
				attachTransactionId()));
		
		if(null != payload && payload.length > 0 && payloadCursor > 0)
			sb.append(String.format(" {%s}", Base64.encode(payload)));
		
		return sb.toString();
	}
	
	private String attachTransactionId() {
		String tid = transactionId();
		return (null == tid)?"":String.format(" tid=%s", tid);
	}

	protected void initPayload() {
		payloadCursor = 0;
		int length = payloadContentLength();
		
		if(length > Configuration.INSTANCE.getMaxPayloadSize())
			throw new RuntimeException(String.format("Received an item (key=%s) with payload size=%d " +
					"but the maximum payload size is %s", params[KEY_POS], length, 
					Configuration.INSTANCE.getMaxPayloadSize()));
		
		if(length >= CRLF)
			payload = new byte[length-CRLF]; 
	}
	
	protected Long getExpire() {
		try{
			return Long.parseLong(params[SetServerCommand.EXPTIME_POS]);
		}catch(NumberFormatException nfe){
			return 0L;
		}
	}

	protected Integer getFlags() {
		try{
			return Integer.parseInt(params[SetServerCommand.FLAGS_POS]);
		}catch(NumberFormatException nfe){
			return 0;
		}
	}

	protected Key getKey() {
		return new Key(params[SetServerCommand.KEY_POS]);
	}

	protected byte[] getPayload() {
		return payload;
	}
	
	protected String transactionId() {
		return null;
	}


	abstract protected String getCommandName();
	
	

}
