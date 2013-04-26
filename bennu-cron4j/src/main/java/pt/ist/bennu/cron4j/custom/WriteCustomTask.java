/*
 * @(#)WriteCustomTask.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Scheduler Module.
 *
 *   The Scheduler Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Scheduler Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Scheduler Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.cron4j.custom;

import java.util.Set;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.io.domain.GenericFile;
import pt.ist.bennu.service.Service;

/**
 * 
 * @author Luis Cruz
 * 
 */
public abstract class WriteCustomTask extends ReadCustomTask {

	private Set<GenericFile> outputFiles;

	void setOutputFiles(final Set<GenericFile> outputFiles) {
		this.outputFiles = outputFiles;
	}

	protected abstract void doService();

	@Override
	public final void doIt() {
		callService();
	}

	@Service
	private void callService() {
		try {
			if (getServerName() != null) {
				VirtualHost.setVirtualHostForThread(getServerName().toLowerCase());
			}
			doService();
		} finally {
			VirtualHost.releaseVirtualHostFromThread();
		}
	}

	/**
	 * Convenience method to more easily use VirtualHosts in these tasks
	 * 
	 * @return the String with the server name of the VirtualHost to use when
	 *         executing this task
	 */
	public String getServerName() {
		return null;
	}

	protected void storeFileOutput(final String displayName, final String filename, final byte[] content, final String contentType) {
//        final CustomTaskOutputFile outputFile = new CustomTaskOutputFile();
//        outputFile.setDisplayName(displayName);
//        outputFile.setFilename(filename);
//        outputFile.setContent(content);
//        outputFile.setContentType(contentType);
//        outputFiles.add(outputFile);
	}

}
