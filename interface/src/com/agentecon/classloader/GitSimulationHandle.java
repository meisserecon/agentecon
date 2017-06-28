package com.agentecon.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

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
		return getURL(JAR_PATH);
	}

	private URL getURL(String path) {
		try {
			return new URL("https://raw.githubusercontent.com/" + getOwner() + "/" + repo + "/" + getName() + "/" + path);
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

//	@Override
//	public URI getURI(String classname) {
//		try {
//			String path = classname.replace('.', '/');
//			int dollar = path.indexOf('$');
//			if (dollar >= 0){
//				path = path.substring(0, dollar);
//			}
//			return getURL("simulation/" + path).toURI();
//		} catch (URISyntaxException e) {
//			throw new java.lang.RuntimeException(e);
//		}
//	}

	@Override
	public InputStream openInputStream(String classname) throws IOException {
		String path = classname.replace('.', '/');
		int dollar = path.indexOf('$');
		if (dollar >= 0){
			path = path.substring(0, dollar);
		}
		URL url = getURL("simulation/src/" + path + ".java");
		return url.openStream();
	}

	@Override
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		throw new RuntimeException("not implemented");
	}
	
}
