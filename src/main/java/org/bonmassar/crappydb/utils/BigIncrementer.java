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
 *  
 * The original version of this class comes from Christian d'Heureuse, 
 * Inventec Informatik AG, Switzerland.
 * Home page: http://www.source-code.biz
 * Source code http://www.source-code.biz/snippets/java/2.htm
 *  
 */

package org.bonmassar.crappydb.utils;

import java.math.BigInteger;

/**
 * Simulate a 64bit unsigned int increment and overflow 
 */
public class BigIncrementer {
	
	private final static BigInteger overflow = new BigInteger("18446744073709551616");

	public static String incr(String a, String b){
		if(b == null)
			return a;
		
		if(a == null)
			a = "";
		
		BigInteger result = getNumber(a).add(getNumber(b));
		if(result.compareTo(overflow) >= 0)
			return result.mod(overflow).toString();
		
		return result.toString();
	}

	private static BigInteger getNumber(String number) {
		try{
			BigInteger result =  new BigInteger(number);
			if(result.compareTo(BigInteger.ZERO) <= 0)
				return BigInteger.ZERO;
			
			return result;
		}catch(NumberFormatException nfe){
			return BigInteger.ZERO;
		}
	}
	
}
