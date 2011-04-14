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

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TestServerCommandNoPayload extends TestCase {

	class ServerCommandNoPayloadImpl extends ServerCommandNoPayload{

		@Override
		protected int getNoReplyPosition() {
			return 0;
		}

		public void execCommand() {}
	}
	
	private ServerCommandNoPayloadImpl nopayload;
	
	@Before
	public void setUp() {
		nopayload = new ServerCommandNoPayloadImpl();
	}
	
	@Test
	public void testShouldThrowExceptionWithEmptyData() {
		try{
			nopayload.addPayloadContentPart("".getBytes());
		}catch(AssertionError e){
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowExceptionWithNoData() {
		try{
			nopayload.addPayloadContentPart(null);
		}catch(AssertionError e){
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowExceptionWithData() {
		try{
			nopayload.addPayloadContentPart("sasdada".getBytes());
		}catch(AssertionError e){
			return;
		}
		fail();
	}
	
	@Test
	public void testSupportNoPayload() {
		assertEquals(0, nopayload.payloadContentLength());
	}
}
