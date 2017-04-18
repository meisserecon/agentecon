// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.api.Event;
import com.agentecon.api.IAgent;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IFirm;
import com.agentecon.api.IMarket;

public class SimulationListenerAdapter implements ISimulationListener {

	@Override
	public void notifyMarketOpened(IMarket market) {
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
	}

	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
	}

	@Override
	public void notifyDayStarted(int day) {
	}

	@Override
	@Deprecated
	public void notifyDayEnded(int day) {
	}

	@Override
	public void notfiyConsumerDied(IConsumer consumer) {
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		notifyDayEnded(day);
	}

	@Override
	public void notifyEvent(Event e) {
	}

	@Override
	public void notifyMarketClosed(IMarket market, boolean fin) {
	}

	@Override
	public void notifyAgentCreated(IAgent agent) {
		if (agent instanceof IFirm) {
			notifyFirmCreated((IFirm) agent);
		} else if (agent instanceof IConsumer) {
			notifyConsumerCreated((IConsumer) agent);
		}
	}

	@Override
	public void notifyAgentDied(IAgent agent) {
		if (agent instanceof IConsumer) {
			notfiyConsumerDied((IConsumer) agent);
		}
	}

}
