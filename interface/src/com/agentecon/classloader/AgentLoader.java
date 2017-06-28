// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.classloader;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.EConsumerType;
import com.agentecon.IAgentFactory;

public class AgentLoader extends ClassLoader {

	public static final String FACTORY_CLASS = "com.agentecon.AgentFactory";

	private SimulationHandle source;
	private AgentCompiler compiler;

	public AgentLoader(RemoteJarLoader simulationJar) throws SocketTimeoutException, IOException {
		this(simulationJar, "meisserecon", "agentecon", "master");
	}

	public AgentLoader(RemoteJarLoader simulationJar, String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		this(simulationJar, new GitSimulationHandle(owner, repo, branch));
	}

	public AgentLoader(File basePath) throws IOException {
		this(null, new LocalSimulationHandle(basePath));
	}

	public AgentLoader(RemoteJarLoader simulationJar, SimulationHandle source) throws SocketTimeoutException, IOException {
		super(AgentLoader.class.getClassLoader());
		this.source = source;
		this.compiler = new AgentCompiler(simulationJar, source);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = this.compiler.findClass(name);
		return super.defineClass(name, data, 0, data.length);
	}

	public SimulationHandle getSourceData() {
		return source;
	}

	public IAgentFactory loadAgentFactory() throws IOException {
		try {
			return (IAgentFactory) loadClass(FACTORY_CLASS).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException("Failed to load simulation", e);
		}
	}

	public static String getType(Class<?> clazz) {
		String name = getName(clazz);
		ClassLoader cl = clazz.getClassLoader();
		if (cl instanceof AgentLoader) {
			return ((AgentLoader) cl).getSourceData().getOwner() + "-" + name;
		} else {
			return name;
		}
	}

	protected static String getName(Class<?> clazz) {
		String name = clazz.getSimpleName();
		while (name.length() == 0) {
			clazz = clazz.getSuperclass();
			name = clazz.getSimpleName();
		}
		return name;
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		IAgentFactory fac = new AgentLoader(new File("../Agents/src/")).loadAgentFactory();
		System.out.println(fac.createConsumer(EConsumerType.HERMIT, null, null));
	}

}
