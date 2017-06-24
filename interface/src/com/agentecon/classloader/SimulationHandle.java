package com.agentecon.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class SimulationHandle {
	
	public static final String JAR_PATH = "jar/simulation.jar";

	private String owner, name;

	public SimulationHandle(String owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
	
	public abstract String getPath();
	
	public abstract long getJarDate() throws IOException;
	
	public abstract InputStream openJar() throws IOException;
	
	public abstract URL getBrowsableURL(String classname);
	
	@Override
	public String toString(){
		return name;
	}

}
