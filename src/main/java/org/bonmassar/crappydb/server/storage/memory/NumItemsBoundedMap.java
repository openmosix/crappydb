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

package org.bonmassar.crappydb.server.storage.memory;

import org.apache.commons.collections.map.LRUMap;
import org.bonmassar.crappydb.server.storage.PAL;
import org.bonmassar.crappydb.server.storage.data.Item;

public class NumItemsBoundedMap extends CollectionPAL {

	static class LRUPALMap extends LRUMap{

		private PAL pal;
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2548149446432744564L;
		
		public LRUPALMap(int numItems){
			super(numItems);
		}
		
		@Override
		protected boolean removeLRU(LinkEntry entry) {
			Item it = (Item)entry.getValue();
			pal.notifyEviction(it);
			return true;
		}
		
		public void setPal(PAL pal){
			this.pal = pal;
		}
	}
	
	@SuppressWarnings("unchecked")
	public NumItemsBoundedMap(int numItems) {
		super(new LRUPALMap(numItems));
		((LRUPALMap)repository).setPal(this);
	}
}
