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

package org.bonmassar.crappydb.server.io;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.apache.commons.cli.ParseException;
import org.bonmassar.crappydb.mocks.FakeSelectionKey;
import org.bonmassar.crappydb.server.ShutdownExecutionRegister.Registry;
import org.bonmassar.crappydb.server.config.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestPoolThreadExecutor extends TestCase {

	private DBPoolThreadExecutor frontend;
	
	@Before
	public void setUp() throws ParseException {
		Registry.INSTANCE.clear();
		Configuration.INSTANCE.parse(null);
		frontend = new DBPoolThreadExecutor(null, null);
	}
	
	@After
	public void tearDown() {
		Registry.INSTANCE.clear();
	}
	
	@Test
	public void testBeingExecuted() throws InterruptedException, ExecutionException{
		List<SelectionKey> keys = getKeys();
		List<Future<Void>> result = new ArrayList<Future<Void>>();
		for (SelectionKey key : keys)
			result.add(frontend.submit(key));
		
		Thread.sleep(5000);
		
		//Not really necessary but to make it explicit
		for (SelectionKey key : keys)
			verify(key, times(4)).readyOps();
		
		for(Future<Void> r : result)
			r.get();
		
		assertEquals(1, Registry.INSTANCE.size());
	}

	private List<SelectionKey> getKeys() {
		List<SelectionKey> keys = new ArrayList<SelectionKey>();
		for(int i = 0; i < 1000; i++){
			SelectionKey key = mock(FakeSelectionKey.class);
			when(key.readyOps()).thenReturn(0);
			keys.add(key);
		}
		
		return keys;
	}
}
