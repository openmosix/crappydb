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
import org.bonmassar.crappydb.server.io.ServerCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;

public abstract class ServerCommand {

	protected StorageAccessLayer storage;
	protected ServerCommandWriter channel;
	
	protected String[] params;
	
	public void parseCommandParams(String commandParams) throws ErrorException{
		if(null == commandParams || commandParams.length() == 0)
			throw new ErrorException("Null parameters");
		
		params = commandParams.trim().split("\\s");
		if(null == params)
			throw new ErrorException("Null parameters");
	}
	
	public abstract int payloadContentLength();
	
	public abstract void addPayloadContentPart(byte[] data);

	public void attachCommandWriter(ServerCommandWriter writer) {
		channel = writer;
	}
	
	public abstract void execCommand();

	public void setStorage(StorageAccessLayer storage) {
		this.storage = storage;
	}
	
}
