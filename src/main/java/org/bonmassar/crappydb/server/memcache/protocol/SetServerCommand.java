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

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.utils.Base64;

//set <key> <flags> <exptime> <bytes> [noreply]\r\n
class SetServerCommand extends ServerCommandAbstract {
	
	private static final int KEY_POS=0;
	private static final int FLAGS_POS=1;
	private static final int EXPTIME_POS=2;
	private static final int BYTES_POS=3;
	private static final int NOREPLY_POS=4;
	private static final int CRLF=2;
	byte[] payload;
	int payloadCursor;
	
	private Logger logger = Logger.getLogger(SetServerCommand.class);

	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		super.parseCommandParams(commandParams);
		if(params.length < 4 || params.length > 5)
			throw new ErrorException("Invalid number of parameters");
		
		payloadCursor = 0;
		int length = payloadContentLength();
		if(length >= CRLF)
			payload = new byte[length-CRLF]; 
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

	public void execCommand() {
		logger.debug("Executed command set");

		Item it = new Item(getKey(), getPayload(), getFlags());
		it.setExpire(getExpire());
		try {
			storage.set(it);
			channel.writeToOutstanding("STORED\r\n");
		} catch (StorageException e) {
			channel.writeToOutstanding(e.toString());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format("{Set key=%s flags=%s expire=%s nbytes=%s noreply=%s}", params[KEY_POS],
				params[FLAGS_POS], params[EXPTIME_POS],params[BYTES_POS],!isResponseRequested()?"true":"false"));
		
		if(null != payload && payload.length > 0 && payloadCursor > 0)
			sb.append(String.format(" {%s}", Base64.encode(payload)));
		
		return sb.toString();
	}
	
	@Override
	protected int getNoReplyPosition() {
		return NOREPLY_POS;
	}
	
	private Long getExpire() {
		try{
			return Long.parseLong(params[SetServerCommand.EXPTIME_POS]);
		}catch(NumberFormatException nfe){
			return 0L;
		}
	}

	private Integer getFlags() {
		try{
			return Integer.parseInt(params[SetServerCommand.FLAGS_POS]);
		}catch(NumberFormatException nfe){
			return 0;
		}
	}

	private Key getKey() {
		return new Key(params[SetServerCommand.KEY_POS]);
	}

	private byte[] getPayload() {
		return payload;
	}

}
