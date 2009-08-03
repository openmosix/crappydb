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

import org.junit.Test;

public class TestFlushAll extends AbstractUseCases {
	
	@Test
	public void testFlush() throws IOException {
		for(int i = 0; i < 10; i++){
			String input = "add terminenzio"+i+" 12 5 2\r\n42\r\n";
			String out = "STORED\r\n";
			testServerInOut(input, out);			
		}
		
		for(int i = 0; i < 10; i++){
			String input = "get terminenzio"+i+"\r\n";
			testServerInMultipleOut(input, new String[]{"VALUE terminenzio"+i+" 12 2\r\n", 
					"42\r\n", "END\r\n"});
		}
		
		String input = "flush_all\r\n";
		String out = "OK\r\n";
		testServerInOut(input, out);
		
		for(int i = 0; i < 10; i++){
			input = "get terminenzio"+i+"\r\n";
			testServerInMultipleOut(input, new String[]{"END\r\n"});
		}
	}
	
	@Test
	public void testFlushNoReply() throws IOException {
		for(int i = 0; i < 10; i++){
			String input = "add terminenzio"+i+" 12 5 2\r\n42\r\n";
			String out = "STORED\r\n";
			testServerInOut(input, out);			
		}
		
		for(int i = 0; i < 10; i++){
			String input = "get terminenzio"+i+"\r\n";
			testServerInMultipleOut(input, new String[]{"VALUE terminenzio"+i+" 12 2\r\n", 
					"42\r\n", "END\r\n"});
		}
		
		String input = "flush_all noreply\r\n";
		testServerNoOutput(input);
		
		pause(2);
		
		for(int i = 0; i < 10; i++){
			input = "get terminenzio"+i+"\r\n";
			testServerInMultipleOut(input, new String[]{"END\r\n"});
		}
	}
	
}
