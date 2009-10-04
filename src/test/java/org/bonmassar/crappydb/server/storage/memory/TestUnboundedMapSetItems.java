package org.bonmassar.crappydb.server.storage.memory;

import java.util.Arrays;

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.SALFactory;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapSetItems  extends TestCase{
	
	private StorageAccessLayer um;
	
	@Before
	public void setUp(){
		um = SALFactory.newInstance(SALFactory.Catalogue.INMEMORY_UNBOUNDED_FIXED_RATE_GC);
	}
	
	@Test
	public void testNullObject() {
		try {
			um.set(null);
		} catch (StorageException e) {
			assertEquals("StorageException [Null item]", e.toString());
		}
	}
		
	@Test
	public void testKeyAdded() {
		try {
			Item it = getDataToSet();
			um.set(it);
			assertEquals(it, um.get(Arrays.asList(it.getKey())).get(0));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testNotFailDataAlreadyExisting() throws NotStoredException, StorageException {
		Item it = getDataToSet();
		um.set(it);
		um.set(it);
	}
	
	private Item getDataToSet(){
		Key k = new Key("Yuppi");
		Item it = new Item (k, "some data".getBytes(), 0);
		return it;
	}

}
