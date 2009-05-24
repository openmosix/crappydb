package org.bonmassar.crappydb.server.storage.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.storage.data.Item;
import org.junit.Before;
import org.junit.Test;

public class TestUnboundedMapAppendItem {
	private UnboundedMap um;
	
	@Before
	public void setUp(){
		um = new UnboundedMap();
	}
	
	@Test
	public void testNullObject() {
		try {
			um.append(null);
			fail();
		} catch (StorageException e) {
			assertEquals("StorageException [Null item]", e.toString());
		}
	}
	
	@Test
	public void testInvalidKey() {
		try {
			Item it = new Item (null, "some data");
			um.append(it);
		} catch (StorageException e) {
			assertEquals("StorageException [Invalid key]", e.toString());
		}
	}
}
