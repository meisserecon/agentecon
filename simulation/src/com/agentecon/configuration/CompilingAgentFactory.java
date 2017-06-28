package com.agentecon.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;

import com.agentecon.EConsumerType;
import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.classloader.CompilingClassLoader;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.RemoteJarLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class CompilingAgentFactory implements IAgentFactory {
	
	private ClassLoader loader;
	
	public CompilingAgentFactory(String owner, String repo) throws SocketTimeoutException, IOException {
		this(owner, repo, "master");
	}

	public CompilingAgentFactory(String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		this.loader = new CompilingClassLoader(getSimulationJarLoader(), new GitSimulationHandle(owner, repo, branch));
	}
	
	public CompilingAgentFactory(File basePath) throws SocketTimeoutException, IOException {
		this.loader = new CompilingClassLoader(basePath);
	}

	private RemoteJarLoader getSimulationJarLoader() {
		ClassLoader loader = getClass().getClassLoader();
		return loader instanceof RemoteJarLoader ? (RemoteJarLoader)loader : null;
	}

	@Override
	public IConsumer createConsumer(EConsumerType type, Endowment endowment, IUtility utilityFunction) {
		try {
			String consumerName = type.getConsumerClassName();
			@SuppressWarnings("unchecked")
			Class<? extends IConsumer> clazz = (Class<? extends IConsumer>) loader.loadClass(consumerName);
			Constructor<? extends IConsumer> constructor = clazz.getConstructor(Endowment.class, IUtility.class);
			return constructor.newInstance(endowment, utilityFunction);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
