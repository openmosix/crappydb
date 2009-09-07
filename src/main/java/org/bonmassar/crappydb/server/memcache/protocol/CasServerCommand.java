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
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.data.Item;

//cas <key> <flags> <exptime> <bytes> <cas unqiue> [noreply]\r\n

class CasServerCommand extends ServerCommandWithPayload {

	private Logger logger = Logger.getLogger(CasServerCommand.class);
	
	private final static int CAS_POS=4;
	
	@Override
	protected String getCommandName() {
		return "Cas";
	}

	public void execCommand() {
		logger.debug("Executed command cas");

		Item it = new Item(getKey(), getPayload(), getFlags(), getExpire());
		try {
			storage.swap(it, transactionId());
			channel.writeToOutstanding("STORED\r\n");
			DBStats.INSTANCE.getProtocol().newSet();
		} catch (CrappyDBException e) {
			channel.writeException(e);
		}
	}
	
	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		minparams = 5;
		maxparams = 6;
		super.parseCommandParams(commandParams);
	}
	
	@Override
	protected int getNoReplyPosition() {
		return 5;
	}
	
	@Override
	protected String transactionId() {
		return params[CAS_POS];
	}

}
