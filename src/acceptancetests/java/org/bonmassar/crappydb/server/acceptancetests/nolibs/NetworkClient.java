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

public class NetworkClient {

	private Socket echoSocket;
	private BufferedReader in;
	private PrintWriter out;

	public NetworkClient() throws UnknownHostException, IOException {
		newConnection();
	}

	public void closeConnection() throws IOException {
		in.close();
		out.close();
		echoSocket.close();
	}
	
	public void resetConnection() throws IOException {
		closeConnection();
	}
	
	public void sendData(String indata) throws IOException {
	    out.println(indata);
    }
	
	public String readline() throws IOException {
		return readline(in);
	}
	
	private void newConnection() throws UnknownHostException, IOException {
		echoSocket = new Socket(AcceptanceConfig.HOST, AcceptanceConfig.SERVERPORT);
        out = new PrintWriter(echoSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
	}
	
	private String readline(BufferedReader in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		do{
			i = in.read();
			sb.append((char)i);
		}while(i!=-1 && i!=10);
		return sb.toString();
	}

}
