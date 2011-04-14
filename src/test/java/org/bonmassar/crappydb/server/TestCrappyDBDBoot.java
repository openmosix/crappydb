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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class TestCrappyDBDBoot {

	private final static String[] rainbow = {"-s", "unbounded-memory-no-gc", "--dump", "--buffer-size", "890", "-p", "20333"};
	private final static String[] help = {"--help"};
	private final static String[] version = {"--version"};
	private final static String[] wrong = {"--cippi-cippi"};
	
	private static class VM implements Runnable{
		private final String[] argv;
		private CrappyDBD db; 
		
		public VM(String[] argv){
			this.argv = argv;
		}
		
		public void run() {
			db = new CrappyDBD();
			db.boot(argv);
		}
		
		public void removeShutdown() {
			Runtime.getRuntime().removeShutdownHook(db.threadsKiller);
			db.threadsKiller.start();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testRainbowBootSequence() throws InterruptedException, UnknownHostException, IOException {
		VM vm = new VM(rainbow);
		Thread t = new Thread(vm);
		t.start();
		Thread.sleep(3000);
		assertTrue(isRunning());
		vm.removeShutdown();
		t.stop();
	}
	
	@Test
	public void testInvalidParamenters() throws InterruptedException, UnknownHostException, IOException {
		VM vm = new VM(wrong);
		Thread t = new Thread(vm);
		t.start();
		Thread.sleep(2000);
		assertEquals(Thread.State.TERMINATED.toString(), t.getState().toString());
	}
	
	@Test
	public void testVersion() throws InterruptedException, UnknownHostException, IOException {
		VM vm = new VM(version);
		Thread t = new Thread(vm);
		t.start();
		Thread.sleep(2000);
		assertEquals(Thread.State.TERMINATED.toString(), t.getState().toString());
	}
	
	@Test
	public void testBootHelp() throws InterruptedException, UnknownHostException, IOException {
		VM vm = new VM(help);
		Thread t = new Thread(vm);
		t.start();
		Thread.sleep(2000);
		assertEquals(Thread.State.TERMINATED.toString(), t.getState().toString());
	}
	
	private boolean isRunning() throws UnknownHostException, IOException {
		Socket echoSocket = new Socket("localhost", 20333);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        out.println("version\r\n");
        boolean result = in.readLine().contains("VERSION");

        out.close();
        in.close();
        echoSocket.close();
        return result;
	}
	
}
