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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.bonmassar.crappydb.server.storage.Expirable;
import org.bonmassar.crappydb.server.storage.data.Key;

class InternalGarbageCollector implements GarbageCollector, Cleaner {
	
	private final Expirable extContainer;
	private final ChangesCollector changes;
	private final Set<ReferenceBean> treemap;
	
	public InternalGarbageCollector(Expirable container, ChangesCollector changes, Set<ReferenceBean> treemap) {
		this.extContainer = container;
		this.changes = changes;
		this.treemap = treemap;
	}
	
	public InternalGarbageCollector(Expirable container) {
		this(container, new ChangesCollector(), new TreeSet<ReferenceBean>());
	}
	
	public void monitor(Key k, long expiration) {
		changes.monitor(k, expiration);
	}

	public void replace(Key k, long oldExpiration, long expiration) {
		changes.replace(k, expiration, oldExpiration);
	}

	public void stop(Key k, long expiration) {
		changes.stop(k, expiration);
	}

	public void expire() {
		Collection<ReferenceBean> victims = getVictims();
		expire(victims);
		updateTimeMapWithIncomingChanges();
	}

	private Collection<ReferenceBean> getVictims() {
		Collection<ReferenceBean> victims = new LinkedList<ReferenceBean>();
		for(Iterator<ReferenceBean> rfit = treemap.iterator(); rfit.hasNext(); ){
			ReferenceBean victim = rfit.next();
			if(!victim.isExpired())
				break;

			victims.add(victim);
			rfit.remove();
		}
		return victims;
	}

	private void expire(Collection<ReferenceBean> victims) {
		if(0 == victims.size())
			return;
		
		for(ReferenceBean victim : victims)
			extContainer.expire(victim.getKey());
	}

	private void updateTimeMapWithIncomingChanges() {
		changes.visitIncoming(treemap);
	}
}
