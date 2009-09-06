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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Set;

import org.bonmassar.crappydb.server.storage.data.Key;
import org.bonmassar.crappydb.server.storage.data.Timestamp;
import org.junit.Test;


import junit.framework.TestCase;

public class TestReplaceReferenceBean extends TestCase {

	@Test
	public void testShouldThrowNPEWithoutKey() {
		try{
			new ReplaceReferenceBean(null, new Timestamp(123L), new Timestamp(456L));
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowNPEWithoutTimestamp() {
		try{
			new ReplaceReferenceBean(new Key("terminenzio"), null, new Timestamp(456L));
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}
	
	@Test
	public void testShouldThrowNPEWithoutOldTimestamp() {
		try{
			new ReplaceReferenceBean(new Key("terminenzio"), new Timestamp(123L), null);
		}catch(NullPointerException npe){
			return;
		}
		fail();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testVisit(){
		Set<ReferenceBean> treemap = mock(Set.class);
		ReferenceBean bean = new ReplaceReferenceBean(new Key("terminenzio"), new Timestamp(1267739498L), new Timestamp(1267739499L));
		bean.visit(treemap);
		verify(treemap, times(1)).remove(new ReferenceBean(new Key("terminenzio"), new Timestamp(1267739499L)));
		verify(treemap, times(1)).add(bean);
	}
	
}
