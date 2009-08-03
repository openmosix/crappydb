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

//flush [time] [noreply]

class FlushServerCommand extends ServerCommandNoPayload {

	private final static int TIME_POS = 0 ;
	private final static int NOREPLY_POS = 0 ;
	
	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		if(null == commandParams || commandParams.length() == 0)
			params = new String[0];
		else
			params = commandParams.trim().split("\\s+");
		
		if(params.length > 2)
			throw new ErrorException("Invalid number of parameters");
	}
	
	@Override
	protected int getNoReplyPosition() {
		return (params.length == 2) ? NOREPLY_POS+1 : NOREPLY_POS;
	}

	public void execCommand() {
		storage.flush(getTime());
		channel.writeToOutstanding("OK\r\n");
	}
	
	private Long getTime() {
		try {
			return (params.length == 0) ? -1L : Long.parseLong(params[FlushServerCommand.TIME_POS]);
		} catch (NumberFormatException nfe) {
			return -1L;
		}
	}
	
	@Override
	public String toString() {
		return String.format("{Flush time=%d noreply=%s}", getTime(), isResponseRequested()?"false":"true" );
	}
}
