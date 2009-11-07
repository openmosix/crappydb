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

import org.bonmassar.crappydb.server.exceptions.StorageException;
import org.bonmassar.crappydb.server.memcache.protocol.NoReplyCommandWriter;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestNoReplyCommandWriter extends TestCase {

	private NoReplyCommandWriter writer;
	
	@Before
	public void setUp() {
		writer = NoReplyCommandWriter.INSTANCE;
	}
	
	@Test
	public void testWriteOnString() {
		writer.writeToOutstanding("blablabla");
		//the only target is to not produce any npe
	}
	
	@Test
	public void testWriteOnData() {
		writer.writeToOutstanding("blablabla".getBytes());
		//the only target is to not produce any npe
	}
	
	@Test
	public void testWriteOnException() {
		writer.writeException(new StorageException("BOOM!"));
		//the only target is to not produce any npe
	}
	
}
