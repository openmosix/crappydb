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

package org.bonmassar.crappydb.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LoopbackMemcacheClient {

	private MemcachedSandbox crappydb;
	private ExternalTestMemcacheSet client;
	
	@Before
	public void setUp(){
		crappydb = new MemcachedSandbox();
		crappydb.run();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client = new ExternalTestMemcacheSet();
	}
	
	@After
	public void tearDown(){
		crappydb.stop();
	}
	
	@Test
	public void testSendSetCommand() {
		String IN = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(IN, OUT);
	}

	private void testServerInOut(String in, String out) {
		String result = client.sendDataToSocket(in);
		assertEquals(out, result);
	}
}
