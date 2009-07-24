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
		assertNull(BigIncrementer.next(null));
	}
	
	@Test
	public void testIncrementEmpty() {
		assertEquals("1", BigIncrementer.next(""));
	}
	
	@Test
	public void testIncrementInvalidNumber() {
		assertEquals("1", BigIncrementer.next("mucca"));
	}
	
	@Test
	public void testIncrementZero() {
		assertEquals("1", BigIncrementer.next("0"));
	}
	
	@Test
	public void testIncrementNegative() {
		assertEquals("1", BigIncrementer.next("-10"));
	}
	
	@Test
	public void testIncrementNormalValue() {
		assertEquals("68944369", BigIncrementer.next("68944368"));
	}
	
	@Test
	public void testIncrementLongValue() {
		assertEquals("58833368944369", BigIncrementer.next("58833368944368"));
	}
	
	@Test
	public void testIncrementUnderOverflow() {
		assertEquals("18446744073709551615", BigIncrementer.next("18446744073709551614"));
	}
	
	@Test
	public void testIncrementOverflow() {
		assertEquals("0", BigIncrementer.next("18446744073709551615"));
	}
	
	@Test
	public void testIncrementOverOverflow() {
		assertEquals("0", BigIncrementer.next("18446744073709551625"));
	}
}
