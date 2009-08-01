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

package org.bonmassar.crappydb.server.acceptancetests.nolibs;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

public class AbstractUseCases {
	protected final static int sleepTimeForAsyncCalls = 3;

	protected NetworkClient client;

	@Before
	public void setUp() throws UnknownHostException, IOException {
		client = new NetworkClient();
	}

	@After
	public void tearDown() throws IOException {
		clean("terminenzio");
		client.closeConnection();
	}

	protected void testServerInOut(String in, String out) throws IOException {
		client.sendData(in);
		assertEquals(out, client.readline());
	}
	
	protected void testServerNoOutput(String input) throws IOException {
		client.sendData(input);		
	}

	
	protected void testServerInMultipleOut(String in, String[] outs) throws IOException{
		client.sendData(in);
		for(int i = 0; i < outs.length; i++)
			assertEquals(outs[i], client.readline());
	}
	
	protected void pause(int sec){
		/* 
		 * The current implementation of the server
		 * process request per channel sequentially
		 * See bug #2 - so there's no need for async wait
		try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	protected void clean(String key) throws IOException{
		client.sendData(String.format("delete %s\r\n", key));
		client.readline();
	}
	
	protected void clean(String[] keys) throws IOException{
		for(String key : keys)
			clean(key);
	}

}
