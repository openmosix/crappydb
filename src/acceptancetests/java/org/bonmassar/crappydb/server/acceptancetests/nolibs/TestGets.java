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

public class TestGets extends AbstractUseCases {

	@Test
	public void testAddAndGets() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddAndGetsChangeFlagsSetGets() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 13 5 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 13 2 3027558\r\n", 
				"42\r\n", "END\r\n"});
	}

	@Test
	public void testAddAndGetsChangeExpiresSetGets() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 4 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027526\r\n", 
				"42\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddAndGetsChangePayloadSetGets() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 5 2\r\n41\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3026566\r\n", 
				"41\r\n", "END\r\n"});
	}
}
