package org.bonmassar.crappydb.server.storage.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.bonmassar.crappydb.server.exceptions.NotStoredException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Cas;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.bonmassar.crappydb.server.storage.data.Key;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapSetItems {
	
	private UnboundedMap um;
	
	@Before
	public void setUp(){
		um = new UnboundedMap();
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
	public void testInvalidKey() {
		try {
			Item it = new Item (null, "some data".getBytes());
			um.set(it);
		} catch (StorageException e) {
			assertEquals("StorageException [Invalid key]", e.toString());
		}
	}
	
	@Test
	public void testKeyAdded() {
		try {
			Item it = getDataToSet();
			um.set(it);
			assertEquals(1, um.repository.size());
			assertEquals(it, um.repository.get(it.getKey()));
			assertNotNull(um.repository.get(it.getKey()).getCas());
			assertTrue(um.repository.get(it.getKey()).getCas().toString().length() > 0);
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
		Item it = new Item (k, "some data".getBytes());
		it.setCas(new Cas(1234L));
		return it;
	}

}
