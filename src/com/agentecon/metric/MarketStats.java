// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.agentecon.api.IIteratedSimulation;
import com.agentecon.api.IMarket;
import com.agentecon.api.ISimulation;
import com.agentecon.api.Price;
import com.agentecon.good.Good;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class MarketStats extends SimStats implements IMarketListener {

	private HashMap<Good, Average> averages;
	// private HashMap<Good, Average> averageOffers;
	private HashMap<Good, TimeSeries> prices;
	// private HashMap<Good, MinMaxTimeSeries> priceBeliefs; // de facto almost same as prices
	private HashMap<Good, TimeSeries> volume;
	private TimeSeries index;

	public MarketStats(boolean inclVolume) {
		this.index = new TimeSeries("Price Index");
		this.averages = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		// this.averageOffers = new InstantiatingHashMap<Good, Average>() {
		//
		// @Override
		// protected Average create(Good key) {
		// return new Average();
		// }
		// };
		this.prices = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries(key.getName());
			}

		};
		// this.priceBeliefs = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {
		//
		// @Override
		// protected MinMaxTimeSeries create(Good key) {
		// return new MinMaxTimeSeries(key.getName());
		// }
		//
		// };
		if (inclVolume) {
			this.volume = new InstantiatingHashMap<Good, TimeSeries>() {

				@Override
				protected TimeSeries create(Good key) {
					return new TimeSeries(key.getName());
				}

			};
		}
	}

	public double getIndex() {
		return index.getLatest();
	}

	public double getPrice(Good good) {
		return averages.get(good).getAverage();
	}

	@Override
	public void notifyMarketOpened(IMarket market) {
		averages.clear();
		market.addMarketListener(this);
	}

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
		// averageOffers.get(good).add(quantity, price.getPrice());
	}

	private String comment;

	@Override
	public void notifySimEnded(ISimulation sim) {
		super.notifySimEnded(sim);
		comment = sim instanceof IIteratedSimulation ? ((IIteratedSimulation) sim).getComment() : null;
	}

	@Override
	public void notifySold(Good good, double quantity, Price price) {
		if (quantity >= 0.001) {
			averages.get(good).add(quantity, price.getPrice());
		}
	}

	@Override
	public void notifyTradesCancelled() {
	}

	@Override
	public void notifyDayEnded(int day) {
		Average indexValue = new Average();
		for (Entry<Good, Average> e : averages.entrySet()) {
			Average avg = e.getValue();
			double price = avg.getAverage();
			double vol = avg.getTotWeight();
			indexValue.add(vol, price);
			prices.get(e.getKey()).set(day, price); // , avg.getMin(), avg.getMax());
			if (volume != null) {
				volume.get(e.getKey()).set(day, vol);
			}
		}
		if (indexValue.hasValue()) {
			index.set(day, indexValue.getAverage());
		}
	}

	@Override
	public String toString() {
		return "prices " + prices.values();
	}

	@Override
	public Collection<? extends Chart> getCharts(long simId) {
		ArrayList<TimeSeries> price = new ArrayList<>(prices.values());
		boolean useIndex = prices.size() > 2;
		if (volume != null && useIndex) {
			price.add(index);
		}
		Chart ch1 = new Chart(simId, comment == null || comment.isEmpty() ? "Prices" : "Prices (" + comment + ")", "Average transacted price for each good", price);
		if (volume == null) {
			return Collections.singleton(ch1);
		} else {
			Chart volumeChart = new Chart(simId, "Trade Volume", "Trade volume for each good", volume.values());
			if (useIndex){
				Chart real = new Chart(simId, "Real Prices", "Nominal prices divided by index", createRealPrices());
				return Arrays.asList(ch1, real, volumeChart);
			} else {
				return Arrays.asList(ch1, volumeChart);
			}
		}
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Price", prices.values()));
		if (volume != null) {
			list.add(index);
			list.add(index.getLogReturns().rename("Inflation rate"));
			list.addAll(createRealPrices());
			list.addAll(TimeSeries.prefix("Volume", volume.values()));
		}
		return list;
	}

	private Collection<? extends TimeSeries> createRealPrices() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (TimeSeries ts : prices.values()) {
			list.add(ts.divideBy(index));
		}
		return list;
	}

}
