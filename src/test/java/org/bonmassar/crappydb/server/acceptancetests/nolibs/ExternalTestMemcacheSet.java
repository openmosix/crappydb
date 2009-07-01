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

package org.bonmassar.crappydb.server.acceptancetests.nolibs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ExternalTestMemcacheSet {
	
	private static String host = "localhost";
	private static int port = 11211;
	
	public String sendDataToSocket(String indata) {
		Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(host, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            return null;
        } catch (IOException e) {
            System.err.println(String.format("Couldn't get I/O for the connection to: %s:%d.", host, port));
            return null;
        }

	    out.println(indata);

	    try {
			String outdata = getString(in);//in.readLine();
			out.close();
			in.close();
			echoSocket.close();
			return outdata;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
    }
	
	public String getString(BufferedReader in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		do{
			i = in.read();
			sb.append((char)i);
		}while(i!=-1 && i!=10);
		return sb.toString();
	}

}
