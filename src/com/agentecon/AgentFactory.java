package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.Producer;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

/**
 * This class provides  
 * @author Luzius
 */
public class AgentFactory implements IAgentFactory {
	
	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return new Consumer("Consumer", endowment, utilityFunction);
	}

	@Override
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun) {
		return new Producer(prodFun.getOutput() + " producer", endowment, prodFun);
	}

	@Override
	public IProducer createFirm(Endowment endowment) {
		return null;
	}

}
