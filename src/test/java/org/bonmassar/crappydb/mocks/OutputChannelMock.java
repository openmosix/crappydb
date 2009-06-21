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

package org.bonmassar.crappydb.mocks;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class OutputChannelMock implements Answer<Integer>{
	
	private List<String> chunks;
	private List<Integer> chunkSizes;
	private int cursor;
	
	public OutputChannelMock() {
		chunks = new ArrayList<String>();
		chunkSizes = new ArrayList<Integer>();
		cursor = 0;
	}
	
	public void transferInChunks(Integer[] sizes){
		if(null == sizes || sizes.length == 0)
			return;
		
		chunkSizes = Arrays.asList(sizes);
	}

	public Integer answer(InvocationOnMock invocation) throws Throwable {
		ByteBuffer buffer = ((ByteBuffer)invocation.getArguments()[0]);
		
		int size = chunkSizes.get(cursor++);
		if(0 != size)
			return readBytes(buffer, size);
		
		chunks.add("");
		return 0;
	}
	
	public boolean dataTransfered(String[] expectedData){
		
		if(null == expectedData && chunks.size() == 0)
			return true;
				
		if(expectedData.length != chunks.size())
			return false;
		
		
		for (int i = 0; i < expectedData.length; i++) {
			if(!expectedData[i].equals(chunks.get(i))){
				System.out.println(i);
				System.out.println("A==>"+expectedData[i]);
				System.out.println("B==>"+chunks.get(i));
				return false;
			}
		}
		
		return true;
	}

	private Integer readBytes(ByteBuffer buffer, int size) {
		byte[] tmp = new byte[size];
		buffer.get(tmp);
		chunks.add(new String(tmp));
		return 1;
	}

}
