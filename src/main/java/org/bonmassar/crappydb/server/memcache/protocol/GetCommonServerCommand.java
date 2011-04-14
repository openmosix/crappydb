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

import java.util.ArrayList;
import java.util.List;

import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.stats.DBStats;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;

abstract class GetCommonServerCommand extends ServerCommandNoPayload {

	public void execCommand() {
		List<Key> keys = getKeys();

		try {
			List<Item> result = storage.get(keys);
			DBStats.INSTANCE.getProtocol().newMisses(keys.size() - result.size());
			writeResult(result);
		} catch (StorageException e) {
			channel.writeException(e);
		}
	}
	
	@Override
	protected int getNoReplyPosition() {
		return -1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format("{%s ", getCommandName()));
		for(int i = 0; i < params.length; i++)
			sb.append(String.format("key%d=%s ", i+1, params[i]));
		
		return sb.append("}").toString();
	}
	
	private void writeResult(List<Item> result) {
		for(Item it : result){
			if(null == it){
				DBStats.INSTANCE.getProtocol().newMisses(1L);
				continue;
			}
			
			writeOneItem(it);
			DBStats.INSTANCE.getProtocol().newHit();
		}
		
		channel.writeToOutstanding("END\r\n");
	}
	
	private List<Key> getKeys() {
		List<Key> keys = new ArrayList<Key>(params.length);
		for(int i = 0; i < params.length; i++)
			keys.add(new Key(params[i]));
		
		return keys;
	}

	protected abstract void writeOneItem(Item it);
	
	protected abstract String getCommandName();

}
