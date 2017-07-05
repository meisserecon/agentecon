package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public interface IAgentFactory {

	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction);
	
}
