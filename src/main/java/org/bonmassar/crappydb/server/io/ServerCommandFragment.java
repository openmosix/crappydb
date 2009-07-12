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

import org.apache.log4j.Logger;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;
import org.bonmassar.crappydb.server.memcache.protocol.ServerCommand;

class ServerCommandFragment {
	private CommandFactory commandFactory; 

	private StringBuilder commandLine;
	private int payloadContentLength;
	private ServerCommand decodedCmd;
	
	private Logger logger = Logger.getLogger(ServerCommandFragment.class);

	private Object connectionid;
	
	ServerCommandFragment(CommandFactory factory) {
		commandFactory = factory;
		connectionid = "unknown";
		reset();
	}
	
	public void reset() {
		payloadContentLength = 0;
		decodedCmd = null;
		commandLine = new StringBuilder();
	}
	
	public void getCommandFromCommandLine() {
		String receivedCommand = removeCrLfOnTail(commandLine.toString());
		
		decodedCmd = commandFactory.getCommandFromCommandLine(receivedCommand);	
		payloadContentLength = decodedCmd.payloadContentLength();
		
		if(logger.isDebugEnabled())
			logger.debug(String.format("[<= ] [%s] Decoded command header: {%s} {payload %d bytes}", connectionid, receivedCommand, payloadContentLength));

	}

	public boolean commandAlreadyDecoded() {
		return null != decodedCmd;
	}
	
	public boolean payloadReadCompleted() {
		return 0 == payloadContentLength;
	}
	
	public ServerCommand getCommand() {
		return decodedCmd;
	}

	public int getContentLength() {
		return payloadContentLength;
	}
	
	public boolean addCommandLineFragment(String line){
		if(null == line)
			return false;
		
		commandLine.append(line);
		if(!isCommandLineCompleted())
			return false;
		
		getCommandFromCommandLine();
		return true;
	}
	
	public void addPayloadContentPart(byte[] data){
		decodedCmd.addPayloadContentPart(data);
		payloadContentLength -= data.length;
	}

	public boolean isCommandLineCompleted() {
		return commandLine.length() > 0 && commandLine.indexOf("\r\n") >= 0;
	}
	
	private String removeCrLfOnTail(String receivedCommand) {
		if(null== receivedCommand || receivedCommand.length()<2)
			return receivedCommand;
			
		return receivedCommand.trim().replace("\r\n", "");	
	}

	public void setConnectionId(String id) {
		connectionid = id;
	}	
}