package com.agentecon.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.consumer.Consumer;
import com.agentecon.firm.Producer;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.Market;
import com.agentecon.market.Price;
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
			Collection<Producer> firms = world.getFirms().getRandomFirms();
			Collection<Consumer> cons = world.getConsumers().getRandomConsumers();
			Market market = new Market(world.getRand());
			market.addMarketListener(observer);
			listeners.notifyMarketOpened(market);
			for (Producer firm : firms) {
				firm.offer(market);
			}
			for (Consumer c : cons) {
				c.maximizeUtility(market);
			}
			for (Producer firm: firms) {
				firm.adaptPrices();
			}
			if (observer.shouldTryAgain()){
				market.notifyCancelled();
				world.abortTransaction();
				listeners.notifyMarketClosed(market, false);
			} else {
				world.commitTransaction();
				listeners.notifyMarketClosed(market, true);
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
		public void notifyOffered(Good good, double quantity, Price price) {
		}
		
		@Override
		public void notifySold(Good good, double quantity, Price price) {
			current.get(good).add(quantity, price.getPrice());
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
		
	}
	
}
