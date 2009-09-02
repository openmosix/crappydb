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
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.data.Item;

// prepend <key> <flags> <exptime> <bytes> [noreply]\r\n

class PrependServerCommand extends ServerCommandWithPayload {

	private Logger logger = Logger.getLogger(PrependServerCommand.class);
	
	@Override
	protected String getCommandName() {
		return "Prepend";
	}

	public void execCommand() {
		logger.debug("Executed command replace");

		Item it = new Item(getKey(), getPayload(), getFlags());
		it.setExpire(getExpire());
		try {
			storage.prepend(it);
			channel.writeToOutstanding("STORED\r\n");
			DBStats.INSTANCE.getProtocol().newSet();
		} catch (CrappyDBException e) {
			channel.writeException(e);
		}
	}
}
