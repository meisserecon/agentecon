package com.agentecon.classloader;

import java.io.IOException;
import java.io.InputStream;
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

	public String getPath(){
		return super.getOwner() + "/" + repo + "/" + getName();
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			return new URL("https://github.com/" + getOwner() + "/" + repo + "/blob/" + getName() + "/src/" + classname.replace(".", "/") + ".java");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public URL getJarURL() {
		try {
			return new URL("https://raw.githubusercontent.com/" + getOwner() + "/" + repo + "/" + getName() + "/" + JAR_PATH);
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public long getJarDate() throws IOException {
		URL url = getJarURL();
		return url.openConnection().getDate();
	}

	@Override
	public InputStream openJar() throws IOException {
		return getJarURL().openStream();
	}
	
}
