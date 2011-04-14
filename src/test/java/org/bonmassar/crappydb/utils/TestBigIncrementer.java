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

import org.junit.Test;

import junit.framework.TestCase;

public class TestBigIncrementer extends TestCase {

	@Test
	public void testIncrementNull() {
		assertNull(BigIncrementer.incr(null, null));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testBaseNull() {
		assertEquals("32", (new BigIncrementer()).incr(null, "32"));
	}
	
	@Test
	public void testIncrementBaseNull() {
		assertEquals("32", BigIncrementer.incr("32", null));
	}
	
	@Test
	public void testIncrementEmpty() {
		assertEquals("32", BigIncrementer.incr("32", ""));
	}
	
	@Test
	public void testIncrementBaseEmpty() {
		assertEquals("32", BigIncrementer.incr("", "32"));
	}
	
	@Test
	public void testIncrementInvalidNumber() {
		assertEquals("32", BigIncrementer.incr("32", "mucca"));
	}
	
	@Test
	public void testIncrementInvalidBaseNumber() {
		assertEquals("32", BigIncrementer.incr("mucca", "32"));
	}
	
	@Test
	public void testIncrementBaseZero() {
		assertEquals("10", BigIncrementer.incr("0", "10"));
	}
	
	@Test
	public void testIncrementZero() {
		assertEquals("10", BigIncrementer.incr("10", "0"));
	}
	
	@Test
	public void testIncrementNegative() {
		assertEquals("910", BigIncrementer.incr("910", "-10"));
	}
	
	@Test
	public void testIncrementBaseNegative() {
		assertEquals("910", BigIncrementer.incr("-10", "910"));
	}
	
	@Test
	public void testIncrementNormalValue() {
		assertEquals("68944370", BigIncrementer.incr("68944368", "2"));
	}
	
	@Test
	public void testIncrementLongValue() {
		assertEquals("58833368944419", BigIncrementer.incr("58833368944368", "51"));
	}
	
	@Test
	public void testIncrementUnderOverflow() {
		assertEquals("18446744073709551615", BigIncrementer.incr("5", "18446744073709551610"));
	}
	
	@Test
	public void testIncrementOverflow() {
		assertEquals("0", BigIncrementer.incr("6", "18446744073709551610"));
	}
	
	@Test
	public void testIncrementOverOverflow() {
		assertEquals("29", BigIncrementer.incr("20", "18446744073709551625"));
	}
}
