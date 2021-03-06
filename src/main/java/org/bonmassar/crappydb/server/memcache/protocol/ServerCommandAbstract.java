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
import org.bonmassar.crappydb.server.io.CommandResponse;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;

abstract class ServerCommandAbstract implements ServerCommand {
	
	protected StorageAccessLayer storage;
	protected CommandResponse channel;
	
	protected String[] params;
	
	protected abstract int getNoReplyPosition();
	
	public void parseCommandParams(String commandParams) throws ErrorException{
		if(null == commandParams || commandParams.length() == 0)
			throw new ErrorException("Null parameters");
		
		params = commandParams.trim().split("\\s+");
		if("".equals(params[0]))
			throw new ErrorException("Null parameters");
	}
		
	public void attachCommandWriter(CommandResponse writer) {
		if(isResponseRequested())
			channel = writer;
		else
			channel = NoReplyCommandWriter.INSTANCE;
	}
	
	public void setStorage(StorageAccessLayer storage) {
		this.storage = storage;
	}
		
	protected boolean isResponseRequested(){
		int noreplypos = getNoReplyPosition();
		return -1 == noreplypos || noreplypos >= params.length 
			|| !"noreply".equals(params[noreplypos]);
	}

}
