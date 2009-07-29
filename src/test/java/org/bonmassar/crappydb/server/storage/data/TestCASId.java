/*  This file is part of CrappyDB-Server, 
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

import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;

public class TestCASId extends TestCase {

	@Test
	public void testGetValueShouldBe57051182679386(){
		//(((((17*31)+10194564)*31)+22)*31)+999999
		
		assertEquals("9798483132", new CASImpl(22, 999999, "This is the payload.".getBytes()).getValue());
	}
	
	@Test
	public void testGetValueShouldBe9798483101(){
		//(((((17*31)+10194564)*31)+21)*311)+999999
		
		assertEquals("9798483101", new CASImpl(
				21, 999999, "This is the payload.".getBytes()).getValue());
	}
	
	@Test
	public void testGetValueShouldBe179973369700(){
		//(((((17*31)-187278748)*31)+22)*31)+999999
		
		assertEquals("179973369700", new CASImpl(
				22, 999999, "this is the payload.".getBytes()).getValue());
	}
	
	@Test
	public void testGetValueShouldBe1507128(){
		//(((((17*31))*31)+22)*31)+999999
		
		assertEquals("1507128", new CASImpl(22, 999999, null).getValue());
	}
	
	@Test
	public void testGetValueShouldBe1506446(){
		//(((((17*31))*31))*31)+999999
		
		assertEquals("1506446", new CASImpl(0, 999999, null).getValue());
	}
	
	@Test
	public void testGetValueShouldBe506447(){
		//(((((17*31))*31))*31)
		
		assertEquals("506447", new CASImpl(0, 0, null).getValue());
	}

	@Test
	public void test4MBPayload(){
		byte[] data = new byte[4*1024*1024];
		for(int i = 0; i < data.length; i++)
			data[i] = (byte)0x42;

		//(((((17*31)-2147483647)*31)+22)*31)+999999
		
		assertEquals("2063730277639", new CASImpl(22, 999999, data).getValue());
	}
	
	@Test
	public void testShouldBeSameObjects() {
		byte[] data1 = new byte[4*1024*1024];
		for(int i = 0; i < data1.length; i++)
			data1[i] = (byte)0x42;
		
		byte[] data2 = new byte[4*1024*1024];
		for(int i = 0; i < data2.length; i++)
			data2[i] = (byte)0x42;
	
		CASId id1 = new CASImpl(22, 999999, data1);
		CASId id2 = new CASImpl(22, 999999, data2);
		assertTrue(id1.compareTo(id2.getValue()));
		assertTrue(id2.compareTo(id1.getValue()));
	}
	
	@Test
	public void testShouldBeDifferentForData() {
		byte[] data1 = new byte[4*1024*1024];
		for(int i = 0; i < data1.length; i++)
			data1[i] = (byte)0x42;
		
		byte[] data2 = new byte[4*1024*1024];
		for(int i = 0; i < data2.length; i++)
			data2[i] = (byte)0x42;
	
		data2[2*1024*1024] = (byte)0x41;
		
		CASId id1 = new CASImpl(22, 999999, data1);
		CASId id2 = new CASImpl(22, 999999, data2);
		assertFalse(id1.compareTo(id2.getValue()));
		assertFalse(id2.compareTo(id1.getValue()));
	}
	
	@Test
	public void testShouldBeDifferentForFlags() {
		byte[] data1 = new byte[4*1024*1024];
		for(int i = 0; i < data1.length; i++)
			data1[i] = (byte)0x42;
		
		byte[] data2 = new byte[4*1024*1024];
		for(int i = 0; i < data2.length; i++)
			data2[i] = (byte)0x42;
			
		CASId id1 = new CASImpl(21, 999999, data1);
		CASId id2 = new CASImpl(22, 999999, data2);
		assertFalse(id1.compareTo(id2.getValue()));
		assertFalse(id2.compareTo(id1.getValue()));
	}
	
	@Test
	public void testShouldBeDifferentForExpiration() {
		byte[] data1 = new byte[4*1024*1024];
		for(int i = 0; i < data1.length; i++)
			data1[i] = (byte)0x42;
		
		byte[] data2 = new byte[4*1024*1024];
		for(int i = 0; i < data2.length; i++)
			data2[i] = (byte)0x42;
			
		CASId id1 = new CASImpl(22, 999997, data1);
		CASId id2 = new CASImpl(22, 999999, data2);
		assertFalse(id1.compareTo(id2.getValue()));
		assertFalse(id2.compareTo(id1.getValue()));
	}
	
	@Test
	public void testShouldBeDifferentForNullObj() {
		byte[] data1 = new byte[4*1024*1024];
		for(int i = 0; i < data1.length; i++)
			data1[i] = (byte)0x42;
		
		CASId id1 = new CASImpl(22, 999997, data1);
		assertFalse(id1.compareTo(null));
	}
	
}
