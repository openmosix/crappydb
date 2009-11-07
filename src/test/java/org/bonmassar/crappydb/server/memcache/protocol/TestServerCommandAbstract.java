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

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.io.CommandResponse;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

public class TestServerCommandAbstract extends TestCase {

	static class CommandAbstractImpl extends ServerCommandAbstract {

		int replyPosition = -1;
		
		@Override
		protected int getNoReplyPosition() { return replyPosition; }

		public void addPayloadContentPart(byte[] data) {}

		public void execCommand() {}

		public int payloadContentLength() {return 0;}
	}
	
	private CommandAbstractImpl command;
	private CommandResponse writer;
	
	@Before
	public void setUp() {
		command = new CommandAbstractImpl();
		writer = mock(CommandResponse.class);
	}
	
	@Test
	public void testShouldThrowExceptionOnNullParams() {
		try {
			command.parseCommandParams(null);
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowExceptionOnSpacesParams() {
		try {
			command.parseCommandParams("        ");
		} catch (ErrorException e) {
			return;
		}
		System.out.println(command.params.length);
		fail();
	}

	@Test
	public void testShouldThrowExceptionOnEmptyParams() {
		try {
			command.parseCommandParams("");
		} catch (ErrorException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldDecode4Params() throws ErrorException {
		command.parseCommandParams("param1 param2    param3    param4");
		assertEquals(4, command.params.length);
		assertEquals("param1", command.params[0]);
		assertEquals("param2", command.params[1]);
		assertEquals("param3", command.params[2]);
		assertEquals("param4", command.params[3]);
	}
	
	@Test
	public void testShouldStripCRLF() throws ErrorException {
		command.parseCommandParams("param1 param2 param3 param4\r\n");
		assertEquals(4, command.params.length);
		assertEquals("param1", command.params[0]);
		assertEquals("param2", command.params[1]);
		assertEquals("param3", command.params[2]);
		assertEquals("param4", command.params[3]);
	}
	
	@Test
	public void testShouldRecognizeOneParameter() throws ErrorException {
		command.parseCommandParams("param1");
		assertEquals(1, command.params.length);
	}
	
	@Test
	public void testShouldRecognizeEmptyHeaderTrailer() throws ErrorException {
		command.parseCommandParams("     param1  param2    param3     param4 ");
		assertEquals(4, command.params.length);
		assertEquals("param1", command.params[0]);
		assertEquals("param2", command.params[1]);
		assertEquals("param3", command.params[2]);
		assertEquals("param4", command.params[3]);
	}
	
	@Test
	public void testShouldSetupStorage() {
		StorageAccessLayer sal = mock(StorageAccessLayer.class);
		command.setStorage(sal);
		assertEquals(command.storage, sal);
	}
	
	@Test 
	public void testShouldInjectAWriterWithNoSupportOfNoReply() throws ErrorException {
		command.parseCommandParams("     param1  param2    param3     param4 ");		
		command.attachCommandWriter(writer);
		assertEquals(writer, command.channel);
	}
	
	@Test 
	public void testShouldInjectAWriterWithNoReplyButNotPresent() throws ErrorException {
		command.parseCommandParams("     param1  param2    param3     param4 ");
		command.replyPosition = 4;
		command.attachCommandWriter(writer);
		assertEquals(writer, command.channel);
	}
	
	@Test 
	public void testShouldInjectAWriterWithNoReplyButWrongParam() throws ErrorException {
		command.parseCommandParams("     param1  param2    param3     param4 ");
		command.replyPosition = 3;
		command.attachCommandWriter(writer);
		assertEquals(writer, command.channel);
	}
	
	@Test 
	public void testShouldInjectAWriterWithNoReply() throws ErrorException {
		command.parseCommandParams("     param1  param2    param3     noreply ");
		command.replyPosition = 3;
		command.attachCommandWriter(writer);
		assertEquals(NoReplyCommandWriter.INSTANCE, command.channel);
	}
}
