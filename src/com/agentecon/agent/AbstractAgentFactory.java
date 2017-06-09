package com.agentecon.agent;

import com.agentecon.IAgentFactory;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

public class AbstractAgentFactory implements IAgentFactory {

	private String name;
	
	public AbstractAgentFactory(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return null;
	}

	@Override
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun) {
		return null;
	}

	@Override
	public IFirm createFirm(Endowment endowment) {
		return null;
	}

}
