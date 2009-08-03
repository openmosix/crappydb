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

package org.bonmassar.crappydb.server.exceptions;

public abstract class CrappyDBException extends Exception{

	private static final long serialVersionUID = -1944815272794785819L;
	private final static String DEFAULT_REASON="No details";
	private String reason;
	
	public CrappyDBException(String reason){
		this.reason = reason;
	}
	
	public CrappyDBException(){
		this.reason = DEFAULT_REASON;
	}
	
	public String toString(){
		return getClass().getSimpleName() + getReason();
	}
	
	public String clientResponse() {
		return toString();
	}

	protected String getReason() {
		if(null != reason && reason.length() > 0)
			return " [" + reason + "]";
		
		return "";
	}
	
}
