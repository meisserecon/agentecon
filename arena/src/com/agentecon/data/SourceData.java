/**
 * Created by Luzius Meisser on Jun 14, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.data;

import java.io.File;
import java.net.URL;

import com.agentecon.agent.IAgent;
import com.agentecon.classloader.RemoteJarLoader;

public class SourceData {
	
	public long date;
	public String owner;
	public String codeLink;
	
	public SourceData(IAgent agent) {
		Class<? extends IAgent> clazz = agent.getClass();
		ClassLoader loader = clazz.getClassLoader();
		if (loader instanceof RemoteJarLoader) {
			RemoteJarLoader agentLoader = (RemoteJarLoader) loader;
			this.date = agentLoader.getDate();
			this.owner = agentLoader.getSourceData().getOwner();
			this.codeLink = agentLoader.getSourceData().getBrowsableURL(agent.getClass().getName()).toExternalForm();
		} else {
			URL url = loader.getResource(clazz.getName().replace('.', File.separatorChar) + ".class");
			File sourceFile = new File(url.toExternalForm().substring("file://".length()).replace(".class", ".java").replace("/bin/", "/src/"));
			this.date = sourceFile.lastModified();
			this.owner = System.getProperty("user.name");
			this.codeLink = "file://" + sourceFile;
		}
	}
	
}
