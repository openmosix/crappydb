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

public class TestCas extends AbstractUseCases {

	@Test
	public void testAddGetsCas() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 1677966698 4 1680994220\r\n4288\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 4102593630\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetCas() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 90 1677966798 4\r\n4288\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 4102593730\r\n", 
				"4288\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 1677966798 8 1680994220\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 4102593730\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetPayloadCas() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 1677966698 2\r\n41\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 1677969998 8 1680994220\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680993259\r\n", 
				"41\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetFlagsCas() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 11 1677966698 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 1677999998 8 1680994220\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 11 2 1680994189\r\n", 
				"42\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetExpireCas() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 1677966699 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 1677969998 8 1680994220\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994221\r\n", 
				"42\r\n", "END\r\n"});
	}

	@Test
	public void testCas() throws IOException {
		String input = "cas terminenzio 90 1677966698 8 3027527\r\n42889922\r\n";
		String out = "NOT_FOUND\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetsCasNoReply() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 1677999998 4 1680994220 noreply\r\n4288\r\n";
		testServerNoOutput(input);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 4102626930\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetCasNoReply() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 90 1677966697 4 3027527\r\n4288\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 4102593629\r\n", 
				"4288\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 1677999998 8 1680994220 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 4102593629\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetPayloadCasNoReply() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 1677966698 2\r\n41\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 1677999998 8 1680994220 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680993259\r\n", 
				"41\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetFlagsCasNoReply() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 11 1677966698 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 1677999998 8 1680994220 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 11 2 1680994189\r\n", 
				"42\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetExpireCasNoReply() throws IOException {
		String input = "add terminenzio 12 1677966698 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994220\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 1677966699 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 1677999998 8 1680994220 noreply\r\n42889922\r\n";
		testServerNoOutput(input);

		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 1680994221\r\n", 
				"42\r\n", "END\r\n"});
	}

	@Test
	public void testCasNoReply() throws IOException {
		String input = "cas terminenzio 90 1677966698 8 3027527 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
	
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"END\r\n"});
	}
}
