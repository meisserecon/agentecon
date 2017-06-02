// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.sim;

import com.agentecon.market.IMarket;
import com.agentecon.util.AbstractListenerList;

public class SimulationListeners extends AbstractListenerList<ISimulationListener> implements ISimulationListener {
	
	@Override
	public void notifyMarketOpened(IMarket market) {
		for (ISimulationListener l: list){
			l.notifyMarketOpened(market);
		}
	}

	@Override
	public void notifyDayStarted(int day) {
		for (ISimulationListener l: list){
			l.notifyDayStarted(day);
		}
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		for (ISimulationListener l: list){
			l.notifyDayEnded(day, utility);
		}
	}

	@Override
	public void notifyEvent(Event e) {
		for (ISimulationListener l: list){
			l.notifyEvent(e);
		}		
	}

	@Override
	public void notifyMarketClosed(IMarket market, boolean fin) {
		for (ISimulationListener l: list){
			l.notifyMarketClosed(market, fin);
		}		
	}

	@Override
	public void notifyAgentCreated(IAgent agent) {
		for (ISimulationListener l: list){
			l.notifyAgentCreated(agent);
		}	
	}

	@Override
	public void notifyAgentDied(IAgent agent) {
		for (ISimulationListener l: list){
			l.notifyAgentDied(agent);
		}	
	}
	
}
