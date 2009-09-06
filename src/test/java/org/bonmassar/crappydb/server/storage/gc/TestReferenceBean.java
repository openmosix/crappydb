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

package org.bonmassar.crappydb.server.storage.gc;

import java.util.Set;

import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.data.Timestamp;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import junit.framework.TestCase;

public class TestReferenceBean extends TestCase {

	private ReferenceBean bean;
	private Key k;
	
	@Before
	public void setUp() {
		k = new Key("terminenzio");
		bean = new ReferenceBean(k, new Timestamp(1267739498L));
	}
	
	@Test
	public void testShouldThrowNPE() {
		try {
			new ReferenceBean(null, new Timestamp(1267739498L));
		} catch (NullPointerException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowNPEWithNoExpire() {
		try {
			new ReferenceBean(new Key("bibibi"), null);
		} catch (NullPointerException e) {
			return;
		}
		fail();
	}
		
	@Test
	public void testGetKeyBasicBean() {
		assertEquals(k, bean.getKey());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testVisit() {
		Set<ReferenceBean> treemap = mock(Set.class);
		bean.visit(treemap);
		verify(treemap, times(1)).add(bean);
	}
		
	@Test
	public void testCompareLess() {
		ReferenceBean bean2 = new ReferenceBean(k, new Timestamp(1267739496L));
		assertTrue((bean.compareTo(bean2))>0);
	}
	
	@Test
	public void testCompareMore() {
		ReferenceBean bean2 = new ReferenceBean(k, new Timestamp(1267739500L));
		assertTrue((bean.compareTo(bean2))<0);
	}
	
	@Test
	public void testCompareEqualsNumLessKey() {
		Key k2 = new Key("terminenzil");
		ReferenceBean bean2 = new ReferenceBean(k2, new Timestamp(1267739498L));
		assertTrue((bean.compareTo(bean2))>0);
	}
	
	@Test
	public void testCompareEqualsNumMoreKey() {
		Key k2 = new Key("terminenzip");
		ReferenceBean bean2 = new ReferenceBean(k2, new Timestamp(1267739498L));
		assertTrue((bean.compareTo(bean2))<0);
	}
	
	@Test
	public void testCompareEqualsNumEqKey() {
		Key k2 = new Key("terminenzio");
		ReferenceBean bean2 = new ReferenceBean(k2, new Timestamp(1267739498L));
		assertEquals(0, bean.compareTo(bean2));
	}
	
}
