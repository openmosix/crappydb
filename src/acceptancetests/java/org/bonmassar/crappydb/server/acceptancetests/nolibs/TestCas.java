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
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 999 4 3027527\r\n4288\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 2424627931\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetCas() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 90 999 4 3027527\r\n4288\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 2424627931\r\n", 
				"4288\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 999 8 3027527\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 2424627931\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetPayloadCas() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 5 2\r\n41\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 999 8 3027527\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3026566\r\n", 
				"41\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetFlagsCas() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 11 5 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 999 8 3027527\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 11 2 3027496\r\n", 
				"42\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetExpireCas() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 6 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 999 8 3027527\r\n42889922\r\n";
		out = "EXISTS\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027528\r\n", 
				"42\r\n", "END\r\n"});
	}

	@Test
	public void testCas() throws IOException {
		String input = "cas terminenzio 90 999 8 3027527\r\n42889922\r\n";
		String out = "NOT_FOUND\r\n";
		testServerInOut(input, out);
	}
	
	@Test
	public void testAddGetsCasNoReply() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 999 4 3027527 noreply\r\n4288\r\n";
		testServerNoOutput(input);
		
		pause(2);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 2424627931\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetCasNoReply() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 90 999 4 3027527\r\n4288\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 2424627931\r\n", 
				"4288\r\n", "END\r\n"});
		
		input = "cas terminenzio 90 999 8 3027527 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
		
		pause(2);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 90 4 2424627931\r\n", 
				"4288\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetPayloadCasNoReply() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 5 2\r\n41\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 999 8 3027527 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
		
		pause(2);
				
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3026566\r\n", 
				"41\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetFlagsCasNoReply() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 11 5 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 999 8 3027527 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
		
		pause(2);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 11 2 3027496\r\n", 
				"42\r\n", "END\r\n"});
	}
	
	@Test
	public void testAddGetsSetExpireCasNoReply() throws IOException {
		String input = "add terminenzio 12 5 2\r\n42\r\n";
		String out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027527\r\n", 
				"42\r\n", "END\r\n"});
		
		input = "set terminenzio 12 6 2\r\n42\r\n";
		out = "STORED\r\n";
		testServerInOut(input, out);
		
		input = "cas terminenzio 90 999 8 3027527 noreply\r\n42889922\r\n";
		testServerNoOutput(input);

		pause(2);
		
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 2 3027528\r\n", 
				"42\r\n", "END\r\n"});
	}

	@Test
	public void testCasNoReply() throws IOException {
		String input = "cas terminenzio 90 999 8 3027527 noreply\r\n42889922\r\n";
		testServerNoOutput(input);
	
		pause(2);
	
		input = "gets terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"END\r\n"});
	}
}
