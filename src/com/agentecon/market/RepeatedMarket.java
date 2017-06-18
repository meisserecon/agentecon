package com.agentecon.market;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.Producer;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarketListener;
import com.agentecon.production.IProducer;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.world.World;

public class RepeatedMarket {

	private final World world;
	private final SimulationListeners listeners;

	public RepeatedMarket(World world, SimulationListeners listeners) {
		this.world = world;
		this.listeners = listeners; 
	}

	public void iterate(int day, int iterations) {
		MarketObserver observer = new MarketObserver(iterations);
		while (true) {
			world.startTransaction();
			Collection<IProducer> firms = world.getAgents().getRandomFirms();
			Collection<IConsumer> cons = world.getAgents().getRandomConsumers();
			Market market = new Market(world.getRand());
			market.addMarketListener(observer);
			listeners.notifyGoodsMarketOpened(market);
			for (IProducer firm : firms) {
				firm.offer(market);
			}
			for (IConsumer c : cons) {
				c.tradeGoods(market);
			}
			for (IProducer firm: firms) {
				firm.adaptPrices();
			}
			if (observer.shouldTryAgain()){
				market.cancel();
				world.abortTransaction();
			} else {
				market.close(day);
				world.commitTransaction();
				break;
			}
		}
	}

	static class MarketObserver implements IMarketListener {
		
		static int count = 0;
		
		private int iters;
		private double sensitivity;
		private HashMap<Good, Average> current;
		private HashMap<Good, Average> prev;

		public MarketObserver(int maxIters) {
			this.iters = maxIters;
			this.sensitivity = 0.001;
			next();
		}

		protected void next() {
			this.prev = current;
			this.current = new InstantiatingHashMap<Good, Average>() {

				@Override
				protected Average create(Good key) {
					return new Average();
				}
			};
		}
		
		@Override
		public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
			current.get(good).add(quantity, payment / quantity);
		}

		@Override
		public void notifyTradesCancelled() {
			current.clear();
		}
		
		public boolean shouldTryAgain() {
			if (iters-- <= 0){
				return false;
			} else if (prev == null){
				next();
				return true;
			} else {
				Average change = new Average();
				for (Map.Entry<Good, Average> e: current.entrySet()){
					double p1 = e.getValue().getAverage();
					double p2 = prev.get(e.getKey()).getAverage();
					double diff = Math.abs(p1 - p2) / p1;
					change.add(diff);
				}
				next();
//				sensitivity *= 1.3;
//				System.out.println(count++);
				return change.getAverage() > sensitivity;
			}
		}

		@Override
		public void notifyMarketClosed(int day) {
		}

	}
	
}
