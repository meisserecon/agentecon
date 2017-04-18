package com.agentecon.sim;

import java.util.Collection;

import com.agentecon.consumer.Consumer;
import com.agentecon.firm.production.Producer;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.market.Market;
import com.agentecon.metric.SimulationListeners;
import com.agentecon.production.IProducer;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.world.Agents;
import com.agentecon.world.World;

public class DailyMarket {

	private final World world;
	private final SimulationListeners listeners;

	public DailyMarket(World world, SimulationListeners listeners) {
		this.world = world;
		this.listeners = listeners;
	}

	public void distributeDividendsEqually(int day, Agents ags) {
		IStock wallet = new Stock(SimConfig.MONEY);
		for (IProducer firm : ags.getAllFirms()) {
			((Producer)firm).payDividends(day, wallet);
		}
		Collection<Consumer> consumers = ags.getAllConsumers();
		double dividend = wallet.getAmount() / consumers.size();
		for (Consumer cons: consumers){
			cons.getMoney().transfer(wallet, dividend);
		}
		if (!wallet.isEmpty()){
			consumers.iterator().next().getMoney().absorb(wallet);
		}
	}
	
	public void trade(int day) {
		Collection<IProducer> firms = world.getFirms().getRandomFirms();
		Collection<Consumer> cons = world.getConsumers().getRandomConsumers();
		Market market = new Market(world.getRand());
		listeners.notifyMarketOpened(market);
		for (IProducer firm : firms) {
			firm.offer(market);
		}
		for (Consumer c : cons) {
			c.trade(market);
		}
		for (IProducer firm : firms) {
			firm.notifyMarketClosed();
		}
		listeners.notifyMarketClosed(market, true);
	}

}
