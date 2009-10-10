package org.bonmassar.crappydb.server.storage.memory;

import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.TestSetItems;
import org.junit.Before;

public class TestUnboundedMapSetItems  extends TestSetItems{
	
	@Before
	public void setUp(){
		um = SALFactory.newInstance(SALFactory.Catalogue.INMEMORY_UNBOUNDED_FIXED_RATE_GC);
	}
}
