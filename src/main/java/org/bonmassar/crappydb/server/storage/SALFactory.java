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
import org.bonmassar.crappydb.server.storage.berkley.data.BerkleyAdapter;
import org.bonmassar.crappydb.server.storage.gc.FixedRateGarbageCollector;
import org.bonmassar.crappydb.server.storage.gc.GarbageCollectorScheduler;
import org.bonmassar.crappydb.server.storage.gc.NullGarbageCollectorScheduler;
import org.bonmassar.crappydb.server.storage.memory.UnboundedMap;


public class SALFactory {
	
	public enum Catalogue {
		INMEMORY_UNBOUNDED_FIXED_RATE_GC("unbounded-memory", UnboundedMap.class, FixedRateGarbageCollector.class),
		INMEMORY_UNBOUNDED_NO_GC("unbounded-memory-no-gc", UnboundedMap.class, NullGarbageCollectorScheduler.class),
		BERKLEY_FIXED_RATE_GC("berkley", BerkleyAdapter.class, FixedRateGarbageCollector.class),
		BERKLEY_NO_GC("berkley-no-gc", BerkleyAdapter.class, NullGarbageCollectorScheduler.class);

		private final static Logger log = Logger.getLogger(SALFactory.class);
		
		private final String storageName;
		private final Class<?> storage;
		private final Class<?> gc;
		
		private Catalogue(String name, Class<?> storage, Class<?> gc) {
			this.storage = storage;
			this.gc = gc;
			this.storageName = name;
		}
		
		public StorageAccessLayer newInstance() {
			PhysicalAccessLayer phystorage = null;
			try {
				phystorage = (PhysicalAccessLayer) storage.newInstance();
			} catch (Exception e) {
				log.fatal("Cannot instantiate required physical access layer", e);
				throw new RuntimeException("Cannot instantiate required physical access layer", e);
			}
			
			StatisticsSAL sal = new StatisticsSAL(phystorage);
			((PAL)phystorage).setSAL(sal);
			GarbageCollectorScheduler scheduler = build(gc, Expirable.class, sal);
			((SALBuilder)sal).setGarbageCollector(scheduler);
			scheduler.startGC();
			return sal;
		}
		
		public static Catalogue parseString(String value){
			if(null == value)
				throw new NullPointerException("Catalogue value is null.");
			
			String valueLw = value.toLowerCase();
			
			for(Catalogue c : Catalogue.values()){
				if(c.storageName.equals(valueLw))
					return c;
			}
			
			return null;
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
		
		@Override
		public String toString() {
			return storageName;
		}
	}

	public static StorageAccessLayer newInstance(Catalogue c){
		return c.newInstance();
	}
	
}
