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
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Key;

// delete <key> [<time>] [noreply]\r\n
class DeleteServerCommand extends ServerCommandNoPayload {

	private static final int KEY_POS=0;
	private static final int TIME_POS=1;
	
	public void parseCommandParams(String commandParams) throws ErrorException {
		super.parseCommandParams(commandParams);
		if(params.length < 1 || params.length > 3)
			throw new ErrorException("Invalid number of parameters");
	}

	public void execCommand() {
		Key k = new Key(params[DeleteServerCommand.KEY_POS]);
		
		try {
			storage.delete(k);
			channel.writeToOutstanding("DELETED\r\n");
		} catch (CrappyDBException e) {
			channel.writeException(e);
		}
	}

	@Override
	protected int getNoReplyPosition() {
		return params.length == 3 ? 2 : 1;
	}
	
	@Override
	public String toString() {
		return String.format("{Delete key=%s time=%d noreply=%s}", params[KEY_POS], getTime(), isResponseRequested()?"false":"true" );
	}

	private Long getTime() {
		try{
			if(params.length > DeleteServerCommand.TIME_POS)
				return Long.parseLong(params[DeleteServerCommand.TIME_POS]);
		}catch(NumberFormatException nfe){
			//ignore and return -1;
		}
		return -1L;

	}

	
}
