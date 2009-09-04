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

public class TestDecrement extends AbstractUseCases {
	
	@Test
	public void testDecr() throws IOException {
		String input = "decr terminenzio 10\r\n";
		String out = "NOT_FOUND\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetDecr() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "decr terminenzio 10\r\n";
		out = "32\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddWrongGetDecr() throws IOException {
		String input = "add terminenzio 12 5 5\r\nmucca\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio 10\r\n";
		out = "0\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetDecrWrong() throws IOException {
		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio -20\r\n";
		out = "5000\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetVeryLargeDecr() throws IOException {
		String input = "add terminenzio 12 5 19\r\n9223372036854780807\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio 9223372036854775807\r\n";
		out = "5000\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetManyDecr() throws IOException {

		String input = "add terminenzio 12 5 6\r\n200000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		for(int i=0, exp=200000; i < 20; i++){
			exp -= 5000;
			input = "decr terminenzio 5000\r\n";
			out = String.format("%d\r\n", exp);
			testServerInOut(input, out);
		}
	}
	
	@Test
	public void testUnderflow() throws IOException {

		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio 9000\r\n";
		out = "0\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testDecrNoReply() throws IOException {
		String input = "decr terminenzio 10 noreply\r\n";
		testServerNoOutput(input);
	}
	
	@Test
	public void testAddGetDecrNoReply() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "decr terminenzio 10 noreply\r\n";
		testServerNoOutput(input);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2\r\n", 
				"32\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddWrongGetDecrNoReply() throws IOException {
		String input = "add terminenzio 12 5 5\r\nmucca\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio 10 noreply\r\n";
		testServerNoOutput(input);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 1\r\n", 
				"0\r\n", "END\r\n"});

	}
	
	@Test
	public void testAddGetDecrWrongNoReply() throws IOException {
		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio -20 noreply\r\n";
		testServerNoOutput(input);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 4\r\n", 
				"5000\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetVeryLargeDecrNoReply() throws IOException {
		String input = "add terminenzio 12 5 19\r\n9223372036854780807\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio 9223372036854775807 noreply\r\n";
		testServerNoOutput(input);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 4\r\n", 
				"5000\r\n", "END\r\n"});
	}
	
	@Test
	public void testUnderflowNoReply() throws IOException {
		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "decr terminenzio 5001 noreply\r\n";
		testServerNoOutput(input);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 1\r\n", 
				"0\r\n", "END\r\n"});
	}
}
