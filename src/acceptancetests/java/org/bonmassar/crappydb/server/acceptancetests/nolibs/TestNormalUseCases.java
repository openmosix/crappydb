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

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestNormalUseCases extends TestCase {

	private final static int sleepTimeForAsyncCalls = 3;
	private NetworkClient client;
	
	@Before
	public void setUp() throws UnknownHostException, IOException{
		client = new NetworkClient();
	}
	
	@After
	public void tearDown() throws IOException {
		client.closeConnection();
	}
	
	@Test
	public void testSetCommand() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		clean("terminenzio");
	}
	
	@Test
	public void testSetCommandNoReply() throws IOException {
		String input = "set terminenzio 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		pause(sleepTimeForAsyncCalls);
		clean("terminenzio");
	}
	
	@Test
	public void testSetAndGetCommand() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", "END\r\n"});
		clean("terminenzio");
	}
	
	@Test
	public void testSetAndGetCommandNoReply() throws IOException {
		String input = "set terminenzio 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		pause(sleepTimeForAsyncCalls);

		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", "END\r\n"});
		clean("terminenzio");
	}
	
	@Test
	public void testMultipleSetAndGetsCommands() throws IOException {
		String input = "set terminenzio1 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "set terminenzio2 24 10 23\r\nThis is another string.\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "set terminenzio3 36 15 15\r\nWhat's up dude?\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "set terminenzio4 48 20 37\r\nThis is the last one and we are done!\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "get terminenzio4 terminenzio3 terminenzio2 terminenzio1\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio4 48 37\r\n", 
				"This is the last one and we are done!\r\n", 
				"VALUE terminenzio3 36 15\r\n", 
				"What's up dude?\r\n",
				"VALUE terminenzio2 24 23\r\n",
				"This is another string.\r\n",
				"VALUE terminenzio1 12 24\r\n",
				"This is simply a string.\r\n",
				"END\r\n"});
	
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}
	
	@Test
	public void testMultipleSetAndGetsCommand() throws IOException {
		String input = "set terminenzio1 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "set terminenzio2 24 10 23\r\nThis is another string.\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "set terminenzio3 36 15 15\r\nWhat's up dude?\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "set terminenzio4 48 20 37\r\nThis is the last one and we are done!\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "gets terminenzio4 terminenzio3 terminenzio2 terminenzio1\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio4 48 37\r\n", 
				"This is the last one and we are done!\r\n", 
				"VALUE terminenzio3 36 15\r\n", 
				"What's up dude?\r\n",
				"VALUE terminenzio2 24 23\r\n",
				"This is another string.\r\n",
				"VALUE terminenzio1 12 24\r\n",
				"This is simply a string.\r\n",
				"END\r\n"});
	
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}

	@Test
	public void testMultipleSetAndGetsCommandsNoReply() throws IOException {
		String input = "set terminenzio1 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		
		input = "set terminenzio2 24 10 23 noreply\r\nThis is another string.\r\n";
		testServerNoOutput(input);
	
		input = "set terminenzio3 36 15 15 noreply\r\nWhat's up dude?\r\n";
		testServerNoOutput(input);
	
		input = "set terminenzio4 48 20 37 noreply\r\nThis is the last one and we are done!\r\n";
		testServerNoOutput(input);
		
		pause(sleepTimeForAsyncCalls);
	
		input = "get terminenzio4 terminenzio3 terminenzio2 terminenzio1\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio4 48 37\r\n", 
				"This is the last one and we are done!\r\n", 
				"VALUE terminenzio3 36 15\r\n", 
				"What's up dude?\r\n",
				"VALUE terminenzio2 24 23\r\n",
				"This is another string.\r\n",
				"VALUE terminenzio1 12 24\r\n",
				"This is simply a string.\r\n",
				"END\r\n"});
	
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}

	@Test
	public void testMultipleSetAndMultipleGetCommands() throws IOException {
		String input = "set terminenzio1 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "set terminenzio2 24 10 23\r\nThis is another string.\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "set terminenzio3 36 15 15\r\nWhat's up dude?\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "set terminenzio4 48 20 37\r\nThis is the last one and we are done!\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
	
		input = "get terminenzio4\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio4 48 37\r\n", 
				"This is the last one and we are done!\r\n", 
				"END\r\n"});
		
		input = "get terminenzio3\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio3 36 15\r\n", 
				"What's up dude?\r\n",
				"END\r\n"});
		
		input = "get terminenzio2\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio2 24 23\r\n",
				"This is another string.\r\n",
				"END\r\n"});
		
		input = "get terminenzio1\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio1 12 24\r\n",
				"This is simply a string.\r\n",
				"END\r\n"});
		
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}

	@Test
	public void testMultipleSetAndMultipleGetCommandsNoReply() throws IOException {
		String input = "set terminenzio1 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		
		input = "set terminenzio2 24 10 23 noreply\r\nThis is another string.\r\n";
		testServerNoOutput(input);
	
		input = "set terminenzio3 36 15 15 noreply\r\nWhat's up dude?\r\n";
		testServerNoOutput(input);
	
		input = "set terminenzio4 48 20 37 noreply\r\nThis is the last one and we are done!\r\n";
		testServerNoOutput(input);
		
		pause(sleepTimeForAsyncCalls);
	
		input = "get terminenzio4\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio4 48 37\r\n", 
				"This is the last one and we are done!\r\n", 
				"END\r\n"});
		
		input = "get terminenzio3\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio3 36 15\r\n", 
				"What's up dude?\r\n",
				"END\r\n"});
		
		input = "get terminenzio2\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio2 24 23\r\n",
				"This is another string.\r\n",
				"END\r\n"});
		
		input = "get terminenzio1\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio1 12 24\r\n",
				"This is simply a string.\r\n",
				"END\r\n"});
		
		clean(new String[]{"terminenzio1", "terminenzio2", "terminenzio3", "terminenzio4"});
	}

	
	@Test
	public void testGetNotExistingElement() throws IOException {
		String input = "get thiskeyisafake\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testSetGetDeleteGet() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		testServerInOut(input, "STORED\r\n");
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", "END\r\n"});
		
		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}

	@Test
	public void testSetGetDeleteGetNoReply() throws IOException {
		String input = "set terminenzio 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);

		pause(sleepTimeForAsyncCalls);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", "END\r\n"});
		
		input = "delete terminenzio noreply\r\n";
		testServerNoOutput(input);
		
		pause(sleepTimeForAsyncCalls);
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}

	@Test
	public void testVersion() throws IOException {
		String input = "version\r\n";
		testServerInOut(input, "VERSION 0.1\r\n");
	}
	
	@Test
	public void testVerbosity() throws IOException {
		String input = "verbosity 0\r\n";
		testServerInOut(input, "OK\r\n");
	}
	
	@Test
	public void testVerbosityNoReply() throws IOException {
		String input = "verbosity 0 noreply\r\n";
		testServerNoOutput(input);
	}
	
	@Test
	public void testAddGetDeleteGet() throws IOException {
		String input = "add terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testSetGetAddGetDeleteGet() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "add terminenzio 12 5 24\r\nThat is really a string.\r\n";
		OUT = "NOT_STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testAddGetDeleteAddGetDelete() throws IOException {
		String input = "add terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	
		input = "add terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testAddNoReplyGetDeleteGet() throws IOException {
		String input = "add terminenzio 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		pause(2);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testSetGetAddNoReplyGetDeleteGet() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "add terminenzio 12 5 24 noreply\r\nThat is really a string.\r\n";
		testServerNoOutput(input);
		pause(2);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testAddNoReplyGetDeleteAddNoReplyGetDelete() throws IOException {
		String input = "add terminenzio 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		
		pause(2);
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	
		input = "add terminenzio 12 5 24 noreply\r\nThis is simply a string.\r\n";
		testServerNoOutput(input);
		pause(2);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testAddGetReplaceGetDeleteGet() throws IOException {
		String input = "add terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);

		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "replace terminenzio 12 5 30\r\nThis is simply another string.\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 30\r\n", 
				"This is simply another string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testSetGetReplaceGetDeleteGet() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);

		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "replace terminenzio 12 5 30\r\nThis is simply another string.\r\n";
		OUT = "STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 30\r\n", 
				"This is simply another string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testReplaceGet() throws IOException {
		String input = "replace terminenzio 12 5 30\r\nThis is simply another string.\r\n";
		String OUT = "NOT_STORED\r\n";
		testServerInOut(input, OUT);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{"END\r\n"});
	}
	
	@Test
	public void testAddGetReplaceGetDeleteGetNoReply() throws IOException {
		String input = "add terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);

		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "replace terminenzio 12 5 30 noreply\r\nThis is simply another string.\r\n";
		testServerNoOutput(input);
		pause(3);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 30\r\n", 
				"This is simply another string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testSetGetReplaceGetDeleteGetNoReply() throws IOException {
		String input = "set terminenzio 12 5 24\r\nThis is simply a string.\r\n";
		String OUT = "STORED\r\n";
		testServerInOut(input, OUT);

		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 24\r\n", 
				"This is simply a string.\r\n", 
				"END\r\n"});
		
		input = "replace terminenzio 12 5 30 noreply\r\nThis is simply another string.\r\n";
		testServerNoOutput(input);
		pause(3);
		
		input = "get terminenzio\r\n";
		testServerInMultipleOut(input, new String[]{
				"VALUE terminenzio 12 30\r\n", 
				"This is simply another string.\r\n", 
				"END\r\n"});

		input = "delete terminenzio\r\n";
		testServerInOut(input, "DELETED\r\n");
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	@Test
	public void testReplaceGetNoReply() throws IOException {
		String input = "replace terminenzio 12 5 30 noreply\r\nThis is simply another string.\r\n";
		testServerNoOutput(input);
		pause(3);
		
		input = "get terminenzio\r\n";
		testServerInOut(input, "END\r\n");
	}
	
	private void testServerInOut(String in, String out) throws IOException {
		client.sendData(in);
		assertEquals(out, client.readline());
	}
	
	private void testServerNoOutput(String input) throws IOException {
		client.sendData(input);		
	}

	
	private void testServerInMultipleOut(String in, String[] outs) throws IOException{
		client.sendData(in);
		for(int i = 0; i < outs.length; i++)
			assertEquals(outs[i], client.readline());
	}
	
	private void pause(int sec){
		try {
			Thread.sleep(sec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void clean(String key) throws IOException{
		testServerInOut(String.format("delete %s\r\n", key), "DELETED\r\n");
	}
	
	private void clean(String[] keys) throws IOException{
		for(String key : keys)
			clean(key);
	}
}
