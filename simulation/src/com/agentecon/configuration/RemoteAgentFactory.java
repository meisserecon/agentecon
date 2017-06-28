package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.EConsumerType;
import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.classloader.RemoteJarLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class RemoteAgentFactory extends RemoteJarLoader implements IAgentFactory {
	
	private IAgentFactory factory;
	
	public RemoteAgentFactory(String owner, String repo) throws SocketTimeoutException, IOException {
		this(owner, repo, "master");
	}

	public RemoteAgentFactory(String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		super(owner, repo, branch);
		try {
			this.factory = (IAgentFactory) loadClass("com.agentecon.AgentFactory").newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IConsumer createConsumer(EConsumerType type, Endowment endowment, IUtility utilityFunction) {
		return this.factory.createConsumer(type, endowment, utilityFunction);
	}

}
