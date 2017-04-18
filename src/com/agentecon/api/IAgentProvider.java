package com.agentecon.api;

import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

public interface IAgentProvider {

	public IConsumer createConsumer();
	
	public IProducer createProducer(IProductionFunction prodFun);
	
}
