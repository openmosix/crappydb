package org.bonmassar.crappydb.mocks;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class TestTest {

	@Test
	public void test() throws IOException{
			    // Create file 
			    FileWriter fstream = new FileWriter("/Users/luca/command");
			        BufferedWriter out = new BufferedWriter(fstream);
			    out.write("version\r\n");
			    //Close the output stream
			    out.close();
	}
}
