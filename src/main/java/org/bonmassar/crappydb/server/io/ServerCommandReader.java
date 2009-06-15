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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.exceptions.CrappyDBException;
import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class ServerCommandReader {
	private int payloadContentLength;
	private ServerCommand decodedCmd;

	protected InputPipe input;
	private Logger logger = Logger.getLogger(ServerCommandReader.class);
	private CommandFactory commandFactory; 
	
	public ServerCommandReader(SelectionKey requestsDescriptor, CommandFactory cmdFactory) {
		payloadContentLength = 0;
		decodedCmd = null;
		commandFactory = cmdFactory;
		input = new InputPipe(requestsDescriptor);
	}

	public List<ServerCommand> decodeCommand() throws CrappyDBException, IOException {
		input.precacheDataFromRemote();
		return decodeIncomingData();
	}

	private List<ServerCommand> decodeIncomingData() throws ErrorException {
		List<ServerCommand> commands = new LinkedList<ServerCommand>();

		ServerCommand cmd = null;
		while(null != (cmd = decodeOneCommand()))
			commands.add(cmd);
		
		return commands;
	}
	
	private ServerCommand decodeOneCommand() throws ErrorException {
		if (input.noDataAvailable() || !decodeCommandFromIncomingData())
			return null;
						
		return commandRead();
	}
	
	private boolean decodeCommandFromIncomingData() throws ErrorException {
		if(commandAlreadyDecoded())
			return true;
		
		String receivedCommand = input.readTextLine();		
		if(null == receivedCommand || 0 == receivedCommand.length())
			return false;
		
		getCommandFromCommandLine(receivedCommand);
		return true;
	}

	private void getCommandFromCommandLine(String receivedCommand)
			throws ErrorException {
		logger.debug("cmd: "+receivedCommand);
		
		decodedCmd = commandFactory.getCommandFromCommandLine(removeCrLfOnTail(receivedCommand));	
		payloadContentLength = decodedCmd.payloadContentLength();
	}

	private boolean commandAlreadyDecoded() {
		return null != decodedCmd;
	}

	private ServerCommand commandRead() {
		if(payloadReadCompleted())
			return commandReadCompleted();
		
		return readCommandPayload();
	}

	private ServerCommand readCommandPayload() {
		if(input.noDataAvailable())
			return null;
			
		decodedCmd.addPayloadContentPart(getPayloadData());
		
		if(payloadReadCompleted())
			return commandReadCompleted();
	
		return null;
	}

	private boolean payloadReadCompleted() {
		return 0 == payloadContentLength;
	}
	

	private String removeCrLfOnTail(String receivedCommand) {
		if(null== receivedCommand || receivedCommand.length()<2)
			return receivedCommand;
			
		return receivedCommand.trim().replace("\r\n", "");	
	}

	private ServerCommand commandReadCompleted() {
		payloadContentLength = 0;
		ServerCommand cmd = decodedCmd;
		decodedCmd = null;
		return cmd;
	}
	
	private byte[] getPayloadData() {		
		byte[] rawdata = input.getBytes(payloadContentLength);
		if(null == rawdata)
			return new byte[0];
		
		payloadContentLength -= rawdata.length;
		return rawdata;
	}
}
