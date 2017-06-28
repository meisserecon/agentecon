package com.agentecon.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class LocalSimulationHandle extends SimulationHandle {

	private static final String JAVA_SUFFIX = ".java";

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

	@Override
	public InputStream openInputStream(String classname) throws IOException {
		String fileName = classname.replace('.', '/') + ".java";
		return new FileInputStream(new File(basePath, fileName));
	}

	@Override
	public Collection<String> listSourceFiles(String packageName) throws IOException {
		File file = new File(basePath, packageName.replace('.', '/'));
		File[] children = file.listFiles();
		ArrayList<String> names = new ArrayList<>();
		for (File f : children) {
			String name = f.getName();
			if (name.endsWith(JAVA_SUFFIX)) {
				name = name.substring(0, name.length() - JAVA_SUFFIX.length());
				names.add(packageName + "." + f.getName());
			}
		}
		throw new RuntimeException("not implemented");
	}

}
