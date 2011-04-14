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


public class TestBigDecrementer extends TestCase {
	
	@Test
	public void testdecrementNull() {
		assertNull(BigDecrementer.decr(null, null));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testBaseNull() {
		assertEquals("0", (new BigDecrementer()).decr(null, "32"));
	}
	
	@Test
	public void testdecrementBaseNull() {
		assertEquals("32", BigDecrementer.decr("32", null));
	}
	
	@Test
	public void testdecrementEmpty() {
		assertEquals("32", BigDecrementer.decr("32", ""));
	}
	
	@Test
	public void testdecrementBaseEmpty() {
		assertEquals("0", BigDecrementer.decr("", "32"));
	}
	
	@Test
	public void testdecrementInvalidNumber() {
		assertEquals("32", BigDecrementer.decr("32", "mucca"));
	}
	
	@Test
	public void testdecrementInvalidBaseNumber() {
		assertEquals("0", BigDecrementer.decr("mucca", "32"));
	}
	
	@Test
	public void testdecrementBaseZero() {
		assertEquals("0", BigDecrementer.decr("0", "10"));
	}
	
	@Test
	public void testdecrementZero() {
		assertEquals("10", BigDecrementer.decr("10", "0"));
	}
	
	@Test
	public void testdecrementNegative() {
		assertEquals("910", BigDecrementer.decr("910", "-10"));
	}
	
	@Test
	public void testdecrementBaseNegative() {
		assertEquals("0", BigDecrementer.decr("-10", "910"));
	}
	
	@Test
	public void testdecrementNormalValue() {
		assertEquals("68944366", BigDecrementer.decr("68944368", "2"));
	}
	
	@Test
	public void testdecrementLongValue() {
		assertEquals("58833368944317", BigDecrementer.decr("58833368944368", "51"));
	}
	
	@Test
	public void testdecrementUnderOverflow() {
		assertEquals("0", BigDecrementer.decr("5", "18446744073709551610"));
	}
	
	@Test
	public void testdecrementOverflow() {
		assertEquals("0", BigDecrementer.decr("18446744073709551630", "18446744073709551625"));
	}
	
	@Test
	public void testdecrementOverOverflow() {
		assertEquals("20", BigDecrementer.decr("20", "18446744073709551625"));
	}
}
