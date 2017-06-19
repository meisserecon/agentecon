package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.e01.Hermit;
import com.agentecon.exercises.e02.Farmer;
import com.agentecon.firm.Producer;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

/**
 * This class provides  
 * @author Luzius
 */
public class AgentFactory implements IAgentFactory {
	
	@Override
	public IConsumer createHermit(Endowment endowment, IUtility utilityFunction, IProductionFunction production) {
		return new Hermit(endowment, utilityFunction, production);
	}
	
	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return new Farmer(endowment, utilityFunction);
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
