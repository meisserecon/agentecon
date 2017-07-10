package com.agentecon.agent;

import java.util.Collection;
import java.util.Set;

import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Ticker;
import com.agentecon.production.IProducer;

public interface IAgents {
	
public Collection<? extends IAgent> getAgents();

	public Collection<? extends IConsumer> getConsumers();
	
	public Collection<? extends IFirm> getFirms();
	
	public Collection<? extends IProducer> getProducers();
	
	public Collection<? extends IShareholder> getShareHolders();
	
	public IFirm getFirm(Ticker ticker);

	public IAgent getAgent(int agentId);

	public Set<String> getFirmTypes();
	
	public Set<String> getConsumerTypes();

	public Collection<IAgent> getAgents(String type);

}
