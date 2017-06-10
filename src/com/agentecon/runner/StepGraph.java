package com.agentecon.runner;

import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.Price;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationListenerAdapter;

public class StepGraph extends SimulationListenerAdapter implements ISimulationListener, IMarketListener {
	
	private ISimulation simulation;

	public StepGraph(ISimulation simulation, List<String> agents) {
		this.simulation = simulation;
		simulation.addListener(this);
	}

	public void fetch(String dataKey) {
		this.simulation.removeListener(this);
	}
	
	@Override
	public void notifyMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}


	@Override
	public void notifyTradesCancelled() {
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		// TODO Auto-generated method stub
		
	}

}
