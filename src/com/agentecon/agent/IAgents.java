package com.agentecon.agent;

import java.util.Collection;

import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Ticker;

public interface IAgents {
	
public Collection<? extends IAgent> getAgents();
	
	public Collection<? extends IConsumer> getConsumers();
	
	public Collection<? extends IFirm> getFirms();
	
	public Collection<? extends IShareholder> getShareHolders();
	
	public Collection<? extends IFirm> getListedCompanies();
	
	public IFirm getListedCompany(Ticker ticker);

	public IAgent getAgent(int agentId);

}
