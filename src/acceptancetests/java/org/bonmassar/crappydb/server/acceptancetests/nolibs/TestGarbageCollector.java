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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Test;

public class TestGarbageCollector extends AbstractUseCases {
	
	@Test
	public void testGarbageCollector() throws IOException {
		String input = "flush_all\r\n";
		testServerInOut(input, "OK\r\n");
		
		System.out.println("Generating data...");
		for(int i = 0; i < 1000; i++){
			input = "set terminenzio"+i+" 12 120 24\r\nThis is simply a string.\r\n";
			testServerInOut(input, "STORED\r\n");
		}
		
		for(int i = 1000; i < 2000; i++){
			input = "set terminenzio"+i+" 12 300 24\r\nThis is simply a string.\r\n";
			testServerInOut(input, "STORED\r\n");
		}
		
		assertEquals("2000", getStoredItems());
		
		System.out.println("Waiting 4 mins...");
		
		pause(4*60);
		
		assertEquals("1000", getStoredItems());

		System.out.println("Waiting 3 mins...");

		pause(3*60);
		
		assertEquals("0", getStoredItems());
	}

	private String getStoredItems() throws IOException {
		client.sendData("stats\r\n");
		
		String gotIt = null;
		String line = null;
		
		while(!(line = client.readline()).equals("END\r\n")){
			if(line.startsWith("STAT curr_items ")){
				gotIt = line.substring(16, line.length()-2);
			}
		}
		
		return gotIt;
	}
}
