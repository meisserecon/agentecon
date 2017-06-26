package com.agentecon.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class LocalSimulationHandle extends SimulationHandle {

	private File basePath;

	public LocalSimulationHandle() {
		this(new File("../simulation"));
	}

	public LocalSimulationHandle(File basePath) {
		super(System.getProperty("user.name").toLowerCase(), "local");
		this.basePath = basePath;
		assert this.basePath.isDirectory() : this.basePath.getAbsolutePath() + " is not a folder";
		assert getJarfile().isFile() : getJarfile().getAbsolutePath() + " does not exist";
	}

	public String getDescription() {
		return "Simulation loader from local file system";
	}

	public String getAuthor() {
		return getOwner();
	}

	private File getJarfile() {
		return new File(basePath, JAR_PATH.replace('/', File.separatorChar));
	}
	
	@Override
	public String getPath() {
		return getOwner() + "/local/local";
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			return new URL("file://" + basePath.getAbsolutePath() + File.separatorChar + "src" + File.separatorChar + classname.replace('.', File.separatorChar) + ".java");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public long getJarDate() throws IOException {
		return getJarfile().lastModified();
	}

	@Override
	public InputStream openJar() throws IOException {
		return new FileInputStream(getJarfile());
	}

}
