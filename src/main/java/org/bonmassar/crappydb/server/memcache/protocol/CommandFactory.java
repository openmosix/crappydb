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

import java.util.HashMap;
import java.util.Map;

import org.bonmassar.crappydb.server.exceptions.ErrorException;
import org.bonmassar.crappydb.server.storage.StorageAccessLayer;

public class CommandFactory {
	
	private Map<String, Class<?>> commands;
	private StorageAccessLayer sal;
	
	public CommandFactory(StorageAccessLayer sal) {
		commands = new HashMap<String, Class<?>>();
		commands.put("add", AddServerCommand.class);
		commands.put("append", AppendServerCommand.class);
		commands.put("delete", DeleteServerCommand.class);
		commands.put("get", GetServerCommand.class);
		commands.put("gets", GetServerCommand.class);
		commands.put("prepend", PrependServerCommand.class);
		commands.put("replace", ReplaceServerCommand.class);
		commands.put("set", SetServerCommand.class);
		commands.put("verbosity", VerbosityServerCommand.class);
		commands.put("version", VersionServerCommand.class);
		this.sal = sal;
	}
	
	public ServerCommand getCommandFromCommandLine(String commandLine) {
		try {
			return findCommandFromCommandLine(commandLine);
		} catch (ErrorException e) {
			return new ExceptionCommand(e);
		}
	}
	
	public ServerCommand createErrorCommand(Exception e) {
		return new ExceptionCommand(new ErrorException(e.getMessage()));
	}
	
	private ServerCommand findCommandFromCommandLine(String commandLine) throws ErrorException{
		if(null == commandLine || commandLine.length() == 0)
			checkInvalidCommand(null);
		
		String cmd = getCommandName(commandLine);
		ServerCommandAbstract serverCmd = getCommand(cmd);
		serverCmd.parseCommandParams(getCommandParams(commandLine));
		return serverCmd;
	}

	protected ServerCommandAbstract getCommand(String cmd) throws ErrorException{
		checkInvalidCommand(cmd);
		Class<?> handler = commands.get(cmd);
		checkValidHandler(handler);
		
		ServerCommandAbstract serverCmd = getNewInstance(handler);
		serverCmd.setStorage(sal);
		
		return serverCmd;
	}

	private ServerCommandAbstract getNewInstance(Class<?> handler) {
		try {
			return (ServerCommandAbstract) handler.newInstance();
		} catch (InstantiationException e) {
			// I should log something
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// I should log something
			e.printStackTrace();
		}
		return null;
	}

	private void checkValidHandler(Class<?> handler) throws ErrorException {
		if(null == handler)
			throw new ErrorException("Command not found");
	}

	private void checkInvalidCommand(String cmd) throws ErrorException {
		if(null == cmd || 0 == cmd.length())
			throw new ErrorException("Invalid command");
	}
	
	private String getCommandName(String commandLine) {
		commandLine = commandLine.trim();
		int firstSpace = commandLine.indexOf(' ');
		if(-1 == firstSpace)
			return commandLine;
		
		return commandLine.substring(0, firstSpace);
	}

	private String getCommandParams(String commandLine) {
		return commandLine.substring(commandLine.indexOf(' ')+1);
	}
	
}
