package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.ex1.AutarkicConsumer;
import com.agentecon.firm.Producer;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

/**
 * This class provides  
 * @author Luzius
 */
public class AgentFactory implements IAgentFactory {
	
	@Override
	public IConsumer createAutarkicConsumer(Endowment endowment, IUtility utilityFunction, IProductionFunction production) {
		return new AutarkicConsumer(endowment, utilityFunction, production);
	}
	
	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return new Consumer(endowment, utilityFunction);
	}

	@Override
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun) {
		return new Producer(endowment, prodFun);
	}

	@Override
	public IProducer createFirm(Endowment endowment) {
		return null;
	}
}
