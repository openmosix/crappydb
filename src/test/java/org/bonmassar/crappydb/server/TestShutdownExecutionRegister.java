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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestShutdownExecutionRegister {
	
	private ExecutorService[]  services;
	
	@Before
	public void setUp() {
		services = new ExecutorService[10];
		for (int i = 0; i < services.length; i++) 
			services[i] = mock(ExecutorService.class);
	}
	
	@After
	public void tearDown() {
		Registry.INSTANCE.clear();
	}

	
	@Test
	public void testShutdownFromThread() {
		for (int i = 0; i < services.length; i++)
			assertTrue(Registry.INSTANCE.book(services[i]));
		
		StorageAccessLayer sal = mock(StorageAccessLayer.class);
		ShutdownExecutionRegister register = new ShutdownExecutionRegister(sal);
		register.run();
		
		assertEquals(0, Registry.INSTANCE.size());
		
		for (int i = 0; i < services.length; i++)
			verify(services[i], times(1)).shutdownNow();
		
		verify(sal, times(1)).close();
	}
}
