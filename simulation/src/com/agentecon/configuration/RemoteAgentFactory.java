package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.classloader.AgentLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

public class RemoteAgentFactory extends AgentLoader implements IAgentFactory {
	
	private IAgentFactory factory;
	
	public RemoteAgentFactory(String owner, String repo) throws SocketTimeoutException, IOException {
		this(owner, repo, "master");
	}

	public RemoteAgentFactory(String owner, String repo, String branch) throws SocketTimeoutException, IOException {
		super(owner, repo, branch);
		this.factory = loadAgentFactory();
	}

	@Override
	public IConsumer createHermit(Endowment endowment, IUtility utilityFunction, IProductionFunction production) {
		return factory.createHermit(endowment, utilityFunction, production);
	}

	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return factory.createConsumer(endowment, utilityFunction);
	}

	@Override
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun) {
		return factory.createProducer(endowment, prodFun);
	}

	@Override
	public IFirm createFirm(Endowment endowment) {
		return factory.createFirm(endowment);
	}

}
