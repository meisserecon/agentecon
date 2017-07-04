package com.agentecon.market;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.util.InstantiatingHashMap;

public class MarketStatistics implements IMarketStatistics, IMarketListener {

	private HashMap<Good, GoodStats> prices;

	public MarketStatistics(){
		this.prices = new InstantiatingHashMap<Good, GoodStats>() {

			@Override
			protected GoodStats create(Good key) {
				return new GoodStats();
			}
		};
	}
	
	@Override
	public Collection<Good> getTradedGoods() {
		return prices.keySet();
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		assert quantity > 0.0;
		prices.get(good).notifyTraded(quantity, payment / quantity);
	}

	@Override
	public void notifyTradesCancelled() {
		for (GoodStats good: prices.values()){
			good.resetCurrent();
		}
	}

	@Override
	public void notifyMarketClosed(int day) {
		for (GoodStats good: prices.values()){
			good.commitCurrent();
		}
	}

	@Override
	public GoodStats getStats(Good good) {
		return prices.get(good);
	}

}
