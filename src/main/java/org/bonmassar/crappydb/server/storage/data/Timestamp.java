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

package org.bonmassar.crappydb.server.storage.data;

/* From protocol spec: 
 * Some commands involve a client sending some kind of expiration time
 * (relative to an item or to an operation requested by the client) to
 * the server. In all such cases, the actual value sent may either be
 * Unix time (number of seconds since January 1, 1970, as a 32-bit
 * value), or a number of seconds starting from current time. In the
 * latter case, this number of seconds may not exceed 60*60*24*30 (number
 * of seconds in 30 days); if the number sent by a client is larger than
 * that, the server will consider it to be real Unix time value rather
 * than an offset from current time.*/

public class Timestamp implements Comparable<Timestamp>{
	//! any time less than this is considered relative, see above
	private final static long relativeTimeThreshold = 60*60*24*30;

	private final long timestamp;
	
	public Timestamp(long timestamp) {
		this.timestamp = getAbsoluteTime(timestamp);
	}
	
	public boolean isExpired() {
		return timestamp > 0 && timestamp < now();
	}
	
	public long getExpire() {
		return timestamp;
	}
	
	public long now() {
		return System.currentTimeMillis() / 1000;
	}

	public int compareTo(Timestamp oth) {
		if(null == oth)
			throw new NullPointerException();
		
		return (int)(timestamp - oth.timestamp);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Timestamp))
			return false;
		
		if(this == obj)	//optimization
			return true;

		return timestamp == ((Timestamp)obj).timestamp;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		return 31 * result + (int)(timestamp ^ (timestamp>>32));
	}
	
	private long getAbsoluteTime(long expire){
		if(expire <= 0)
			return 0;
		
		if(expire <= Timestamp.relativeTimeThreshold)
			return now() + expire;
		
		return expire;
	}
}
