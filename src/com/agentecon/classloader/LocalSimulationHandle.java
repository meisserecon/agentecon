package com.agentecon.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class LocalSimulationHandle extends SimulationHandle {

	private File basePath;

	public LocalSimulationHandle() {
		this(new File("../AgenteconSim"));
	}

	public LocalSimulationHandle(File basePath) {
		super(System.getProperty("user.name"), "Local");
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
	public URL getJarURL() {
		try {
			return new URL("file://" + getJarfile().getAbsolutePath());
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public URL getBrowsableURL(String classname) {
		try {
			return new URL("file://" + basePath.getAbsolutePath() + File.separatorChar + classname.replace('.', File.separatorChar));
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

}
