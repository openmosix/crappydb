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

public class GetServerCommand extends ServerCommand {

	public static String getCmdName() {
		return "get";
	}

	@Override
	public void parseCommandParams(String commandParams) throws ErrorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int payloadContentLength() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void addPayloadContentPart(byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execCommand() {
		// TODO Auto-generated method stub
		
	}
}
