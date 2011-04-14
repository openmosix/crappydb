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

import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

// incr <key> <value> [noreply]\r\n

class IncrServerCommand extends ServerCommandNoPayload {

	private static final int KEY_POS=0;
	private static final int VAL_POS=1;
	private static final int NOREPLY_POS=2;
	
	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		super.parseCommandParams(commandParams);
		if(params.length < 2 || params.length > 3)
			throw new ErrorException("Invalid number of parameters");
	}
	
	@Override
	protected int getNoReplyPosition() {
		return NOREPLY_POS;
	}

	public void execCommand() {
		Key k = new Key(params[IncrServerCommand.KEY_POS]);
		
		try {
			Item it = storage.increase(k, params[VAL_POS]);
			if(null == it)
				throw new StorageException("Internal Error");
			channel.writeToOutstanding(it.getData());
			channel.writeToOutstanding("\r\n");
		} catch (CrappyDBException e) {
			channel.writeException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("{Incr key=%s value=%s noreply=%s}", params[0], params[1], !isResponseRequested()?"true":"false");
	}
}
