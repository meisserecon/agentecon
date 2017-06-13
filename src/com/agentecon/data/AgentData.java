package com.agentecon.data;

import java.io.File;
import java.net.URL;

import com.agentecon.agent.IAgent;
import com.agentecon.classloader.AgentLoader;

public class AgentData {

	public long date;
	public String owner;
	public String codeLink;
	
	public double money;
	public String agentName;

	public AgentData(IAgent agent) {
		Class<? extends IAgent> clazz = agent.getClass();
		ClassLoader loader = clazz.getClassLoader();
		if (loader instanceof AgentLoader) {
			AgentLoader agentLoader = (AgentLoader) loader;
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
		this.money = agent.getMoney().getAmount();
		this.agentName = agent.getName();
	}

}
