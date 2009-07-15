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

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Key;

// delete <key> [<time>] [noreply]\r\n
public class DeleteServerCommand extends ServerCommandAbstract {

	private static final int KEY_POS=0;
	
	public static String getCmdName() {
		return "delete";
	}

	public void parseCommandParams(String commandParams) throws ErrorException {
		super.parseCommandParams(commandParams);
		if(0 == params.length)
			throw new ErrorException("Invalid number of parameters");
	}

	public int payloadContentLength() {
		return 0;
	}

	public void addPayloadContentPart(byte[] data) {
		throw new IllegalArgumentException();		
	}

	public void execCommand() {
		Key k = getKey(params[DeleteServerCommand.KEY_POS]);
		if(null == k)
			channel.writeToOutstanding("Invalid key\r\n".getBytes());
		
		try {
			storage.delete(k);
			channel.writeToOutstanding("DELETED\r\n".getBytes());
		} catch (NotFoundException e) {
			channel.writeToOutstanding(e.toString().getBytes());
		} catch (StorageException e) {			
			channel.writeToOutstanding(e.toString().getBytes());
		}
	}
	
	private Key getKey(String key) {
		if(null == key || key.length() == 0 )
			return null;
		
		return new Key(key);
	}

	
}
