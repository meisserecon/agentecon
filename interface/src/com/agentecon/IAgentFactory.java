package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public interface IAgentFactory {

	public IConsumer createConsumer(EConsumerType type, Endowment endowment, IUtility utilityFunction);
	
}
