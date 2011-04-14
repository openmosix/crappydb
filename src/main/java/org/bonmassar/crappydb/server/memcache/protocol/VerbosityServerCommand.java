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
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.bonmassar.crappydb.server.exceptions.ErrorException;

// verbosity <lognumvalue> [noreply]
// Please note 
class VerbosityServerCommand extends ServerCommandNoPayload {

	private static final int LEVEL_POS = 0;
	Logger logger = Logger.getLogger(VerbosityServerCommand.class);
	private int level = 0;
	
	@Override
	protected int getNoReplyPosition() {
		return 1;
	}
	
	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		super.parseCommandParams(commandParams);
		if(params.length > 2)
			throw new ErrorException("Invalid number of parameters");
	
		level = getLevel();
	}
	
	public void execCommand() {
		if(-1 == level)
			channel.writeException(new ErrorException("Invalid verbosity level"));
		
		logger.info(String.format("Switching log to level %d", level));
		logger.setLevel(intToLog4jLevel(level));
		channel.writeToOutstanding("OK\r\n");
	}
	

	@Override
	public String toString() {
		if(-1 == level)
			return "{Verbosity log=-1}";
		
		return String.format("{Verbosity log=%s}", intToLog4jLevel(level).toString());
	}

	private int getLevel() {
		try{
			return Integer.parseInt(params[VerbosityServerCommand.LEVEL_POS]);
		}catch(NumberFormatException nfe){
			return -1;
		}
	}

	private Level intToLog4jLevel(int severity) {
		switch (severity){
			case 7: return Level.ALL;
			case 6: return Level.TRACE;
			case 5: return Level.DEBUG;
			case 4: return Level.INFO;
			case 3: return Level.WARN;
			case 2: return Level.ERROR;
			case 1: return Level.FATAL;
			case 0: return Level.OFF;
		}
		if(severity > 7)
			return Level.ALL;
		
		return Level.OFF;
	}
}
