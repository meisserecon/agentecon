// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.api.Event;
import com.agentecon.api.IAgent;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.api.IMarket;

public interface ISimulationListener {

	public void notifyDayStarted(int day);
	
	@Deprecated
	public void notifyDayEnded(int day);
	
	public void notifyDayEnded(int day, double utility);

	public void notifyMarketOpened(IMarket market);
	
	public void notifyMarketClosed(IMarket market, boolean finalIteration);
	
	@Deprecated
	public void notifyFirmCreated(IFirm firm);

	@Deprecated
	public void notifyConsumerCreated(IConsumer consumer);
	
	public void notifyAgentCreated(IAgent agent);

	@Deprecated
	public void notfiyConsumerDied(IConsumer consumer);
	
	public void notifyAgentDied(IAgent agent);
	
	public void notifyEvent(Event e);
	
}
