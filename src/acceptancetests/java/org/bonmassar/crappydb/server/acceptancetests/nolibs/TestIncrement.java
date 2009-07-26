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

public class TestIncrement extends AbstractUseCases {
	@Test
	public void testIncr() throws IOException {
		String input = "incr terminenzio 10\r\n";
		String out = "NOT_FOUND\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetIncr() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "incr terminenzio 10\r\n";
		out = "52\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddWrongGetIncr() throws IOException {
		String input = "add terminenzio 12 5 5\r\nmucca\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "incr terminenzio 10\r\n";
		out = "10\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetIncrWrong() throws IOException {
		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "incr terminenzio -20\r\n";
		out = "5000\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetVeryLargeIncr() throws IOException {
		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "incr terminenzio 9223372036854775807\r\n";
		out = "9223372036854780807\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetManyIncr() throws IOException {

		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		for(int i=0, exp=5000; i < 20; i++){
			exp += 5000;
			input = "incr terminenzio 5000\r\n";
			out = String.format("%d\r\n", exp);
			testServerInOut(input, out);
		}
	}
	
	@Test
	public void testOverflow() throws IOException {

		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "incr terminenzio 18446744073709551316\r\n";
		out = "4700\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testCompleteOverflow() throws IOException {

		String input = "add terminenzio 12 5 4\r\n5000\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);

		input = "incr terminenzio 18446744073709551616\r\n";
		out = "5000\r\n";
		testServerInOut(input, out);
	}
}
