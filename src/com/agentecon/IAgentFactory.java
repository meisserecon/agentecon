package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

public interface IAgentFactory {

	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction);
	
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun);
	
	public IProducer createFirm(Endowment endowment);
	
}
