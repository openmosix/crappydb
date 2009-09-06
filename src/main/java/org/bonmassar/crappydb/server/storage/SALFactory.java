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

package org.bonmassar.crappydb.server.storage;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.storage.gc.FixedRateGarbageCollector;
import org.bonmassar.crappydb.server.storage.gc.GarbageCollectorScheduler;
import org.bonmassar.crappydb.server.storage.gc.NullGarbageCollectorScheduler;
import org.bonmassar.crappydb.server.storage.memory.UnboundedMap;


public class SALFactory {
	
	public enum Catalogue {
		INMEMORY_UNBOUNDED_FIXED_RATE_GC(SALImpl.class, UnboundedMap.class, FixedRateGarbageCollector.class),
		INMEMORY_UNBOUNDED_NO_GC(SALImpl.class, UnboundedMap.class, NullGarbageCollectorScheduler.class);

		private final static Logger log = Logger.getLogger(SALFactory.class);
		
		private final Class<?> sal;
		private final Class<?> storage;
		private final Class<?> gc;
		
		private Catalogue(Class<?> sal, Class<?> storage, Class<?> gc) {
			this.sal = sal;
			this.storage = storage;
			this.gc = gc;
		}
		
		public StorageAccessLayer newInstance() {
			StorageAccessLayer intstorage = null;
			try {
				intstorage = (StorageAccessLayer) storage.newInstance();
			} catch (Exception e) {
				log.fatal("Cannot instantiate required sal", e);
			}
			
			StorageAccessLayer salimpl = build( sal, StorageAccessLayer.class, intstorage);
			GarbageCollectorScheduler scheduler = build(gc, Expirable.class, salimpl);
			((SALBuilder)salimpl).setGarbageCollector(scheduler);
			scheduler.startGC();
			return salimpl;
		}
		
		
		@SuppressWarnings("unchecked")
		private <E, T> E build(Class<?> type, Class<?> paramType, T constructorParam){
			Constructor<E> argConstructor;
			try {
				argConstructor = (Constructor<E>) type.getConstructor(paramType);
				return (E) argConstructor.newInstance(constructorParam);
			} catch (Exception e) {
				log.fatal("Cannot instantiate required sal", e);
				return null;
			}
		}
	}

	public static StorageAccessLayer newInstance(Catalogue c){
		return c.newInstance();
	}
	
}
