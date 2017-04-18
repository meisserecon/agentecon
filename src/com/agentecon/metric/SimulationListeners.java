// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.api.Event;
import com.agentecon.api.IAgent;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.api.IMarket;
import com.agentecon.metric.ISimulationListener;

public class SimulationListeners extends AbstractListenerList<ISimulationListener> implements ISimulationListener {
	
	@Override
	public void notifyFirmCreated(IFirm firm) {
		for (ISimulationListener l: list){
			l.notifyFirmCreated(firm);
		}
	}

	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
		for (ISimulationListener l: list){
			l.notifyConsumerCreated(consumer);
		}
	}

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
	public void notifyDayEnded(int day) {
		for (ISimulationListener l: list){
			l.notifyDayEnded(day);
		}
	}

	@Override
	public void notfiyConsumerDied(IConsumer consumer) {
		for (ISimulationListener l: list){
			l.notfiyConsumerDied(consumer);
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
