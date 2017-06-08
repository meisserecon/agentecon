package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

public class AgentFactory implements IAgentFactory {
	
	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return null;
	}

	@Override
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun) {
		return null;
	}

	@Override
	public IProducer createFirm(Endowment endowment) {
		return null;
	}

}
