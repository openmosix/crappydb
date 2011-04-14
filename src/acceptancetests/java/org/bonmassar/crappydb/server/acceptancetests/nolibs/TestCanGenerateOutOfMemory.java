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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class TestCanGenerateOutOfMemory extends AbstractUseCases {

	protected NetworkClient client2;
	protected ExecutorService parallel;

	@Override
	public void setUp() throws UnknownHostException, IOException {
		super.setUp();
		client2 = new NetworkClient(10*1000);
		parallel = Executors.newFixedThreadPool(1);
	}
	
	@Override
	public void tearDown() {
		super.tearDown();
		if(!parallel.isShutdown())
			parallel.shutdownNow();
		try {
			client2.closeConnection();
		} catch (IOException e) {
		}
	}
	
	@Test
	public void testShouldNotFreezeDBWithInvalidContentLength() throws IOException {
		
		parallel.execute(new Runnable() {

			public void run() {
				try {
					client2.sendData("cas terminenzio 90 1677966698 1677966698 1680994220\r\n4288\r\n");
					client2.readline();
				} catch (IOException e) {}
			}
			
		});
		pause(1);
		for(int i = 0; i < 5; i++){
			testServerInOut("get terminenzio4 terminenzio3 terminenzio2 terminenzio1\r\n", "END\r\n");
			pause(1);
		}
	}
	
}
