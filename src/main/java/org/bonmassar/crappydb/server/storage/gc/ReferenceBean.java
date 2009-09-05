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

class ReferenceBean implements Comparable<ReferenceBean>{
	protected final Key key;
	protected final Long expire;
	
	public ReferenceBean(Key k, long expire) {
		if(null == k)
			throw new NullPointerException();
		
		this.key = k;
		this.expire = expire;	//autoboxing
	}
	
	public void visit(final Set<ReferenceBean> timerlist) {
		timerlist.add(this);
	}
	
	public Key getKey() {
		return key;
	}

	public int compareTo(ReferenceBean in) {
		if(null == in)
			throw new NullPointerException();
		
		int compareLong = expire.compareTo(in.expire);
		if(0 != compareLong)
			return  compareLong;
		
		return key.compareTo(in.key);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	public boolean isExpired() {
		// TODO Auto-generated method stub
		return false;
	}
}
