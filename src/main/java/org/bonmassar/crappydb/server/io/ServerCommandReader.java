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

import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

public class ServerCommandReader {

	protected InputPipe input;
	private ServerCommandFragment lastCommand;
	
	public ServerCommandReader(SelectionKey requestsDescriptor, CommandFactory cmdFactory) {
		input = new InputPipe(requestsDescriptor);
		lastCommand = new ServerCommandFragment(cmdFactory);
	}

	public List<ServerCommand> decodeCommand() throws IOException{
		input.precacheDataFromRemote();
		return decodeIncomingData();
	}

	private List<ServerCommand> decodeIncomingData() {
		List<ServerCommand> commands = new LinkedList<ServerCommand>();

		ServerCommand cmd = null;
		while(null != (cmd = decodeOneCommand()))
			commands.add(cmd);
		
		return commands;
	}
	
	private ServerCommand decodeOneCommand() {
		if (input.noDataAvailable() || !decodeCommandFromIncomingData())
			return null;
						
		return commandRead();
	}
	
	private boolean decodeCommandFromIncomingData() {
		if(lastCommand.commandAlreadyDecoded())
			return true;
		
		return lastCommand.addCommandLineFragment(input.readTextLine());
	}

	private ServerCommand commandRead() {
		if(lastCommand.payloadReadCompleted())
			return commandReadCompleted();
		
		return readCommandPayload();
	}

	private ServerCommand readCommandPayload() {
		if(input.noDataAvailable())
			return null;
			
		lastCommand.addPayloadContentPart(getPayloadData());
		
		if(lastCommand.payloadReadCompleted())
			return commandReadCompleted();
	
		return null;
	}	

	private ServerCommand commandReadCompleted() {
		ServerCommand cmd = lastCommand.getCommand();
		lastCommand.reset();
		return cmd;
	}
	
	private byte[] getPayloadData() {		
		byte[] rawdata = input.getBytes(lastCommand.getContentLength());
		if(null == rawdata)
			return new byte[0];
		
		return rawdata;
	}
}
