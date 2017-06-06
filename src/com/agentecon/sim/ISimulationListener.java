// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.sim;

import com.agentecon.agent.IAgent;
import com.agentecon.market.IMarket;

public interface ISimulationListener {

	public void notifyDayStarted(int day);
	
	public void notifyDayEnded(int day, double utility);

	public void notifyMarketOpened(IMarket market);
	
	public void notifyMarketClosed(IMarket market, boolean finalIteration);
	
	public void notifyAgentCreated(IAgent agent);

	public void notifyAgentDied(IAgent agent);
	
	public void notifyEvent(Event e);
	
}
