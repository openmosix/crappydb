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

package org.bonmassar.crappydb.server.storage.gc;

import java.util.Set;

import org.bonmassar.crappydb.server.storage.data.Key;

class ReplaceReferenceBean extends ReferenceBean {

	private final Long oldExpire;

	public ReplaceReferenceBean(Key k, long expire, long oldExpire) {
		super(k, expire);
		this.oldExpire = oldExpire;
	}

	@Override
	public void visit(Set<ReferenceBean> timerlist) {
		timerlist.remove(new ReferenceBean(key, oldExpire));
		timerlist.add(this);
	}

}
