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

package org.bonmassar.crappydb.server.io;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.Callable;
import org.bonmassar.crappydb.server.memcache.protocol.CommandFactory;

public class FrontendPoolExecutor extends PoolThreadExecutor<SelectionKey> {
	private final static int nFrontendThreads=1;
	private static CommandFactory cmdFactory;
	private static Selector serverSelectorForAccept;
	private static BackendPoolExecutor backend;
	
	public FrontendPoolExecutor() {
		super(FrontendPoolExecutor.nFrontendThreads);
	}
	
	@Override
	protected Callable<Integer> createNewTask() {
		return new FrontendTask(cmdFactory, serverSelectorForAccept, backend, queue);
	}
	
	public static void setup(CommandFactory cmdFactory, Selector serverSelectorForAccept, BackendPoolExecutor backend){
		FrontendPoolExecutor.cmdFactory = cmdFactory;
		FrontendPoolExecutor.serverSelectorForAccept = serverSelectorForAccept;
		FrontendPoolExecutor.backend = backend;
	}
}
