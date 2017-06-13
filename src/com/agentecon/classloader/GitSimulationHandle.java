package com.agentecon.classloader;

import java.net.MalformedURLException;
import java.net.URL;

public class GitSimulationHandle extends SimulationHandle {

	private String repo;

	public GitSimulationHandle(String owner, String repo, String name) {
		super(owner, name);
		this.repo = repo;
//		String commitUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/commits/" + name;
//		String commitDesc = WebUtil.readHttp(commitUrl);
//		this.date = WebUtil.extract(commitDesc, "date", 0);
	}

	@Override
	public String getOwner() {
		return repo;
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			return new URL("https://github.com/" + getOwner() + "/" + repo + "/blob/" + getName() + "/src/" + classname.replace(".", "/") + ".java");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public URL getJarURL() {
		try {
			return new URL("https://api.github.com/" + getOwner() + "/" + repo + "/contents/" + JAR_PATH + "?ref=" + getName());
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}
	
}
