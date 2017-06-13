// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.runner;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.ISimulation;
import com.agentecon.classloader.AgentLoader;
import com.agentecon.classloader.SimulationHandle;

public class SimulationLoader extends AgentLoader {

	private static final String SIM_CLASS = "com.agentecon.Simulation";

	private Checksum checksum;

	public SimulationLoader(File basePath) throws IOException {
		super(basePath);
	}
	
	public SimulationLoader(SimulationHandle handle) throws SocketTimeoutException, IOException {
		super(handle);
	}

	public Checksum getChecksum(){
		return checksum;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends ISimulation> loadSimClass() {
		try {
			return (Class<? extends ISimulation>) loadClass(SIM_CLASS);
		} catch (ClassNotFoundException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public ISimulation loadSimulation() throws IOException {
		try {
			return (ISimulation) loadClass(SIM_CLASS).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException("Failed to load simulation" , e);
		}
	}

}
