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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestVeryVeryLongKey {
	
	private String keyWith250Chars = "Nelmezzodelcammindinostravitamiritrovai" +
	"perunaselvaoscurachéladirittaviaerasmarrita." +
	"Ahiquantoadirqualeraècosaduraestaselvaselvaggiaeasp" +
	"raefortechenelpensierrinovalapaura!Tant'èamarachepocoèpiùmorte;" +
	"mapertrattardelbench'i'vitrovai,diròdel'altrecosech'i";
	
	@Test
	public void test250CharKey(){
		Key k =	new Key(keyWith250Chars);

		assertEquals(keyWith250Chars, k.toString());
	}
	
	@Test
	public void test251CharKey(){
		Key k =	new Key(keyWith250Chars+"a");

		assertEquals(keyWith250Chars, k.toString());
	}
	
	@Test
	public void test250CharKeyButWhiteSpace(){
		Key k =	new Key("    \r\r \n   \t"+keyWith250Chars);

		assertEquals(keyWith250Chars, k.toString());
	}
}
