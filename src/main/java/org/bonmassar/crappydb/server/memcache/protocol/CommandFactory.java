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

public class CommandFactory {
	
	private Map<String, Class<?>> commands;
	
	public CommandFactory() {
		commands = new HashMap<String, Class<?>>();
		commands.put(SetServerCommand.getCmdName(), SetServerCommand.class);
	}
	
	public ServerCommand getCommand(String cmd) throws ErrorException{
		checkInvalidCommand(cmd);
		Class<?> handler = commands.get(cmd);
		checkValidHandler(handler);
		
		/*
		    Constructor c = A.class.getConstructor(new Class[]{Integer.TYPE,
			Float.TYPE});
			A a = (A)c.newInstance(new Object[]{new Integer(1), new Float(1.0f)});
		 */
		
		return getNewInstance(handler);
	}

	private ServerCommand getNewInstance(Class<?> handler) {
		try {
			return (ServerCommand) handler.newInstance();
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
	
}
