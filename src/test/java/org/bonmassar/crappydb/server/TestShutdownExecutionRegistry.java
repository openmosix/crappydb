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
import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;

import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import junit.framework.TestCase;


public class TestShutdownExecutionRegistry extends TestCase {

	private ExecutorService service;
	
	@Before
	public void setUp() {
		service = mock(ExecutorService.class);
	}
	
	@After
	public void tearDown() {
		Registry.INSTANCE.clear();
	}
	
	@Test
	public void testBookWithMockObj() throws Exception {
		assertTrue(Registry.INSTANCE.book(service));
		assertEquals(1, Registry.INSTANCE.size());
	}
	
	@Test
	public void testBookNullObj() {
		assertFalse(Registry.INSTANCE.book(null));
		assertEquals(0, Registry.INSTANCE.size());
	}
	
	@Test
	public void testShutdown() {
		ExecutorService[] services = new ExecutorService[10];
		for (int i = 0; i < services.length; i++) {
			services[i] = mock(ExecutorService.class);
			assertTrue(Registry.INSTANCE.book(services[i]));
		}
		
		Registry.INSTANCE.shutdown();
		assertEquals(0, Registry.INSTANCE.size());
		
		for (int i = 0; i < services.length; i++)
			verify(services[i], times(1)).shutdownNow();
	}
		
}
