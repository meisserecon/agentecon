// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.api.Event;
import com.agentecon.api.IAgent;
import com.agentecon.api.IMarket;

public class SimulationListenerAdapter implements ISimulationListener {

	@Override
	public void notifyMarketOpened(IMarket market) {
	}

	@Override
	public void notifyDayStarted(int day) {
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
	}

	@Override
	public void notifyEvent(Event e) {
	}

	@Override
	public void notifyMarketClosed(IMarket market, boolean fin) {
	}

	@Override
	public void notifyAgentCreated(IAgent agent) {
	}

	@Override
	public void notifyAgentDied(IAgent agent) {
	}

}
