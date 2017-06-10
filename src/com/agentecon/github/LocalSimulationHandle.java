package com.agentecon.github;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class LocalSimulationHandle extends SimulationHandle {

	private File simFile;
	
	public LocalSimulationHandle() throws IOException, InterruptedException{
		this("../AgenteconSim/jar/agents.jar");
	}
	
	public LocalSimulationHandle(String path) throws IOException, InterruptedException {
		super(System.getProperty("user.name"), "Local");
		this.simFile = new File(path);
		assert simFile.isFile();
	}

	public String getDescription() {
		return "Simulation loader from local file system";
	}

	public String getAuthor() {
		return getOwner();
	}

	public String getDate() {
		return new Date(simFile.lastModified()).toString();
	}

	@Override
	public InputStream open() throws IOException {
		return new FileInputStream(simFile);
	}
	
}
