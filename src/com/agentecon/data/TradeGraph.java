package com.agentecon.data;

import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationListenerAdapter;

public class TradeGraph extends SimulationListenerAdapter implements ISimulationListener, IMarketListener {
	
	private ISimulation simulation;

	public TradeGraph(ISimulation simulation, List<String> agents) {
		this.simulation = simulation;
		simulation.addListener(this);
	}

	public void fetch(String dataKey) {
		this.simulation.removeListener(this);
	}
	
	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyTradesCancelled() {
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		
	}

	@Override
	public void notifyMarketClosed(int day) {
	}

}
