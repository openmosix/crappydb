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

package org.bonmassar.crappydb.server.memcache.protocol;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.TreeMap;

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.exceptions.NotFoundException;
import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.io.OutputCommandWriter;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestStatsCommand extends TestCase {

	class MockStatsCommand extends StatsCommand {
		@Override
		protected Map<String, String> getStats() {
			Map<String, String> map = new TreeMap<String, String>();
			map.put("key1", "val1");
			map.put("key2", "val2");
			map.put("key3", "val3");
			map.put("key4", "val4");
			map.put("key5", "val5");
			map.put("key6", "val6");
			map.put("key7", "val7");
			map.put("key8", "val8");
			map.put("key9", "val9");
			map.put("key10", "val10");
			return map;
		}
	}
	
	private StatsCommand command;
	private StorageAccessLayer storage;
	private OutputCommandWriter output;
	
	@Before
	public void setUp() {
		command = new MockStatsCommand();
		storage = mock(StorageAccessLayer.class);
		output = mock(OutputCommandWriter.class);
		command.setStorage(storage);
		command.channel = output;
	}
	
	@Test
	public void testShouldThrowExceptionWithTooManyParams() {
		try {
			command.parseCommandParams("90\r\n");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldNotSupportNoReply() throws ErrorException {
		assertEquals(-1, command.getNoReplyPosition());
	}
		
	@Test
	public void testRainbow() throws ErrorException, NotFoundException, StorageException {
		command.parseCommandParams("");
		
		command.execCommand();

		verify(output, times(1)).writeToOutstanding("STAT key1 val1\r\nSTAT key10 val10\r\nSTAT key2 val2\r\nSTAT key3 val3\r\nSTAT key4 val4\r\nSTAT key5 val5\r\nSTAT key6 val6\r\nSTAT key7 val7\r\nSTAT key8 val8\r\nSTAT key9 val9\r\nEND\r\n");
	}
	
	@Test
	public void testToString() throws ErrorException {
		command.parseCommandParams("");
		
		assertEquals("{Stats}", command.toString());
	}	
}
