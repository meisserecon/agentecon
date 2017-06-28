// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.classloader;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class CompilingClassLoader extends RemoteLoader {

	private AgentCompiler compiler;

	public CompilingClassLoader(RemoteJarLoader simulationJar) throws SocketTimeoutException, IOException {
		this(simulationJar, "meisserecon", "agentecon", "master");
	}

	public CompilingClassLoader(RemoteJarLoader simulationJar, String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		this(simulationJar, new GitSimulationHandle(owner, repo, branch));
	}

	public CompilingClassLoader(File basePath) throws IOException {
		this(null, new LocalSimulationHandle(basePath));
	}

	public CompilingClassLoader(RemoteJarLoader simulationJar, SimulationHandle source) throws SocketTimeoutException, IOException {
		super(CompilingClassLoader.class.getClassLoader(), source);
		this.compiler = new AgentCompiler(simulationJar, source);
		if (simulationJar != null){
			simulationJar.addDependentLoader(this);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = this.compiler.findClass(name);
		return super.defineClass(name, data, 0, data.length);
	}

}
