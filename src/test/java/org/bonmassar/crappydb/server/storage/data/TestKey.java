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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

public class TestKey {
	
	private Key key;
	
	@Before
	public void setUp() {
		key = new Key("abcdef1234ghilmn");
	}
	
	@Test
	public void testKeysAreEquals(){
		assertEquals(key, new Key("abcdef1234ghilmn"));
	}
	
	@Test
	public void testKeysIdentityIsEqual(){
		assertEquals(key, key);
	}
	
	@Test
	public void testKeysAreEqualsWithWhitespaces(){
		assertEquals(key, new Key("          abcdef1234ghilmn        "));
	}
	
	@Test
	public void testKeysAreEqualsWithControlChars(){
		assertEquals(key, new Key("         \n\r  abcdef1234ghilmn  \t\r\n      "));
	}
	
	@Test
	public void testKeysAreEqualsWithOtherObject(){
		assertFalse(key.equals(new String("abcdef1234ghilmn")));
	}
	
	@Test
	public void testKeysAreEqualsWithNullObject(){
		assertFalse(key.equals(null));
	}
	
	@Test
	public void testCompareLess() {
		Key key2 = new Key("abcdef1234ghilmm");
		assertTrue(key.compareTo(key2)>0);

	}
	
	@Test
	public void testCompareMore() {
		Key key2 = new Key("abcdef1234ghilmo");
		assertTrue(key.compareTo(key2)<0);
	}
	
	@Test
	public void testCompareEquals() {
		Key key2 = new Key("abcdef1234ghilmn");
		assertTrue(key.compareTo(key2)==0);
	}
	
	@Test
	public void testHashCode() {
		assertEquals(-1675158567, key.hashCode());
	}
	
	@Test
	public void testCompareShouldThrowNPE() {
		try{
			key.compareTo(null);
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}

}
