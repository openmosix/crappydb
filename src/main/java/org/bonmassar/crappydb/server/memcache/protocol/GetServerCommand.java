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

import org.bonmassar.crappydb.server.storage.data.Item;

// get <key>*\r\n

class GetServerCommand extends GetCommonServerCommand {

	@Override
	protected String getCommandName() {
		return "Get";
	}

	protected void writeOneItem(Item it) {
		byte[] data = it.getData();
		int length = (data != null) ? data.length : 0;
		channel.writeToOutstanding(String.format("VALUE %s %d %d\r\n", it.getKey(), it.getFlags(), length));
		
		if(length > 0)
			channel.writeToOutstanding(data);
		
		channel.writeToOutstanding("\r\n");
	}

}
