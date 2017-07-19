package com.agentecon.metric.minichart;

import java.io.IOException;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.web.query.AgentQuery;

public class TradeMiniChart extends MiniChart {

	private Good good;
	private AgentQuery source, dest;

	public TradeMiniChart(AgentQuery source, AgentQuery dest, Good good) {
		this.source = source;
		this.dest = dest;
		this.good = good;
	}
	
	@Override
	protected float getData(SimulationStepper stepper, int day) throws IOException {
		ISimulation sim = stepper.getSimulation(day - 1);
		MarketListener priceListener = new MarketListener();
		ISimulationListener listener = createListener(priceListener);
		sim.addListener(listener);
		sim.forwardTo(day);
		sim.removeListener(listener);
		return (float) priceListener.getPrice();
	}

	private ISimulationListener createListener(IMarketListener priceListener) {
		return new SimulationListenerAdapter(){
			@Override
			public void notifyGoodsMarketOpened(IMarket market) {
				market.addMarketListener(priceListener);
			}
		};
	}

	@Override
	protected String getName() {
		return "Selected price of " + good;
	}
	
	class MarketListener implements IMarketListener {
		
		private Average price;
		
		public MarketListener(){
			this.price = new Average();
		}

		@Override
		public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
			if (good == TradeMiniChart.this.good){
				if (source.matches(seller) && dest.matches(buyer)){
					assert quantity >= 0.0;
					price.add(payment / quantity);
				}
			}
		}

		@Override
		public void notifyTradesCancelled() {
			this.price = new Average();
		}

		@Override
		public void notifyMarketClosed(int day) {
		}
		
		public double getPrice(){
			return price.getAverage();
		}
		
	}

}
