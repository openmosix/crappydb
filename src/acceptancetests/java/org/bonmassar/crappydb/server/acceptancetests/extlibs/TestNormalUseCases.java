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

package org.bonmassar.crappydb.server.acceptancetests.extlibs;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;

import org.bonmassar.crappydb.server.acceptancetests.nolibs.AcceptanceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestNormalUseCases extends TestCase {
	
	private MemCachedClient client;
	private SockIOPool pool;
	
	@Before 
	public void setUp() {
		String[] serverlist = { AcceptanceConfig.HOST+":"+AcceptanceConfig.SERVERPORT };

        pool = SockIOPool.getInstance();
        pool.setServers(serverlist);
        pool.initialize();      
        
		client = new MemCachedClient();
		// compression is enabled by default    
        client.setCompressEnable(true);
        // set compression threshold to 4 KB (default: 15 KB)  
        client.setCompressThreshold(4096);
	}
	
	@After
	public void tearDown() throws IOException {
		clean("terminenzio");

		pool.shutDown();
	}
	
	@Test
	public void testSetCommand() throws IOException {
		assertTrue(client.set("terminenzio", "This is simply a string", 12));
		
		clean("terminenzio");
	}
	
	@Test
	public void testSetAndGetCommand() throws IOException {
		assertTrue(client.set("terminenzio", "This is simply a string", 12));
		Object res = client.get("terminenzio");
		
		assertEquals("This is simply a string", ((String)res));

		clean("terminenzio");
	}
	
	@Test
	public void testMultipleSetAndGetsCommands() throws IOException {
		assertTrue(client.set("terminenzio1", "This is simply a string", 12));
		assertTrue(client.set("terminenzio2", "This is another string.", 24));
		assertTrue(client.set("terminenzio3", "What's up dude?", 36));
		assertTrue(client.set("terminenzio4", "This is the last one and we are done!", 48));

		Map<String, Object> results = client.getMulti(new String[]{"terminenzio4","terminenzio3","terminenzio2", "terminenzio1"});

		assertNotNull(results);
		assertEquals(4, results.size());
		assertEquals("This is simply a string", results.get("terminenzio1"));
		assertEquals("This is another string.", results.get("terminenzio2"));
		assertEquals("What's up dude?", results.get("terminenzio3"));
		assertEquals("This is the last one and we are done!", results.get("terminenzio4"));
	
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}

	@Test
	public void testMultipleSetAndMultipleGetCommands() throws IOException {
		assertTrue(client.set("terminenzio1", "This is simply a string", 12));
		assertTrue(client.set("terminenzio2", "This is another string.", 24));
		assertTrue(client.set("terminenzio3", "What's up dude?", 36));
		assertTrue(client.set("terminenzio4", "This is the last one and we are done!", 48));
	
		assertEquals("This is simply a string", (String) client.get("terminenzio1"));
		assertEquals("This is another string.", (String) client.get("terminenzio2"));
		assertEquals("What's up dude?", (String) client.get("terminenzio3"));
		assertEquals("This is the last one and we are done!", (String) client.get("terminenzio4"));
		
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}
	
	@Test
	public void testGetNotExistingElement() throws IOException {
		assertNull((String) client.get("thiskeyisafake"));
	}
	
	
	@Test
	public void testSetGetDeleteGet() throws IOException {
		assertTrue(client.set("terminenzio", "This is simply a string", 12));
		assertEquals("This is simply a string", ((String)client.get("terminenzio")));
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
	}
	
	@Test
	public void testAddGetReplaceGetDeleteGet() throws IOException {
		assertTrue(client.add("terminenzio", "This is simply a string", new Date()));
		assertEquals("This is simply a string", ((String)client.get("terminenzio")));
		assertTrue(client.replace("terminenzio", "This is another string", new Date()));
		assertEquals("This is another string", ((String)client.get("terminenzio")));
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
	}
	
	@Test
	public void testSetGetReplaceGetDeleteGet() throws IOException {
		assertTrue(client.set("terminenzio", "This is the first value", new Date()));
		assertEquals("This is the first value", ((String)client.get("terminenzio")));
		
		assertTrue(client.replace("terminenzio", "This is another string", new Date()));
		assertEquals("This is another string", ((String)client.get("terminenzio")));
		
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
	}
	
	@Test
	public void testReplaceGet() throws IOException {
		assertFalse(client.replace("terminenzio", "This is simply a string", new Date()));
		assertEquals(null, ((String)client.get("terminenzio")));
	}
	
	@Test
	public void testAddGetDeleteGet() throws IOException {
		assertTrue(client.add("terminenzio", "This is simply a string", new Date()));
		assertEquals("This is simply a string", ((String)client.get("terminenzio")));
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
	}
	
	@Test
	public void testSetGetAddGetDeleteGet() throws IOException {
		assertTrue(client.set("terminenzio", "This is the first value", new Date()));
		assertEquals("This is the first value", ((String)client.get("terminenzio")));
		
		assertFalse(client.add("terminenzio", "This is simply a string", new Date()));
		assertEquals("This is the first value", ((String)client.get("terminenzio")));
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
	}
	
	@Test
	public void testAddGetDeleteAddGetDelete() throws IOException {
		assertTrue(client.add("terminenzio", "This is simply a string", new Date()));
		assertEquals("This is simply a string", ((String)client.get("terminenzio")));
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
		
		assertTrue(client.add("terminenzio", "This is simply a string", new Date()));
		assertEquals("This is simply a string", ((String)client.get("terminenzio")));
		assertTrue(client.delete("terminenzio"));
		assertNull((String) client.get("terminenzio"));
	}
	
	@Test
	public void testIncr() {
		assertEquals(-1L, client.incr("terminenzio", 10L));
	}
	
	@Test
	public void testAddGetIncr() {
		assertTrue(client.add("terminenzio", "42"));
		assertEquals(52L, client.incr("terminenzio", 10L));
	}
	
	@Test
	public void testAddWrongGetIncr() {
		assertTrue(client.add("terminenzio", "mucca"));
		assertEquals(10L, client.incr("terminenzio", 10L));
	}
	
	@Test
	public void testAddGetIncrWrong() {
		assertTrue(client.add("terminenzio", "5000"));
		assertEquals(5000L, client.incr("terminenzio", -20L));
	}
	
	@Test
	public void testAddGetVeryLargeIncr() {
		assertTrue(client.add("terminenzio", "5000"));
		assertEquals(Long.MAX_VALUE, client.incr("terminenzio", Long.MAX_VALUE-5000L));
	}
	
	@Test
	public void testAddGetManyIncr() {
		assertTrue(client.add("terminenzio", "5000"));
		for(int i=0, exp=5000; i < 20; i++){
			exp += 5000;
			assertEquals(exp, client.incr("terminenzio", 5000L));
		}
	}
	
	@Test
	public void testDecr() {
		assertEquals(-1L, client.decr("terminenzio", 10L));
	}
	
	@Test
	public void testAddGetDecr() {
		assertTrue(client.add("terminenzio", "42"));
		assertEquals(32L, client.decr("terminenzio", 10L));
	}
	
	@Test
	public void testAddGetDecrUnderflow() {
		assertTrue(client.add("terminenzio", "42"));
		assertEquals(0L, client.decr("terminenzio", 180L));
	}
	
	@Test
	public void testAddWrongGetDecr() {
		assertTrue(client.add("terminenzio", "mucca"));
		assertEquals(0L, client.decr("terminenzio", 10L));
	}
	
	@Test
	public void testAddGetDecrWrong() {
		assertTrue(client.add("terminenzio", "5000"));
		assertEquals(5000L, client.decr("terminenzio", -20L));
	}
	
	@Test
	public void testAddGetVeryLargeDecr() {
		assertTrue(client.add("terminenzio", "9223372036854780807"));
		assertEquals(5000L, client.decr("terminenzio", Long.MAX_VALUE));
	}
	
	@Test
	public void testAddGetManyDecr() {
		assertTrue(client.add("terminenzio", "200000"));
		for(int i=0, exp=200000; i < 20; i++){
			exp -= 5000;
			assertEquals(exp, client.decr("terminenzio", 5000L));
		}
	}
		
	private void clean(String key) throws IOException{
		client.delete(key);
	}
	
	private void clean(String[] keys) throws IOException{
		for(String key : keys)
			clean(key);
	}

}
