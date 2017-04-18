package com.agentecon.metric;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.api.IFirm;
import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.InstantiatingHashMap;

public class MarketMetrics extends SimStats implements IMarketListener, IFirmListener {

	private static final double MEMORY = 0.95;

	private boolean stable;
	private AccumulatingAverage dividends, profits;
	private HashMap<Good, AccumulatingAverage> prices;
	private HashMap<Good, AccumulatingAverage> production;
	private ArrayList<AccumulatingAverage> all;

	public MarketMetrics() {
		this.all = new ArrayList<>();
		this.dividends = new AccumulatingAverage(MEMORY);
		this.all.add(dividends);
		this.profits = new AccumulatingAverage(MEMORY);
		this.all.add(profits);
		this.prices = new InstantiatingHashMap<Good, AccumulatingAverage>() {

			@Override
			protected AccumulatingAverage create(Good key) {
				AccumulatingAverage wma = new AccumulatingAverage(MEMORY);
				all.add(wma);
				return wma;
			}
		};
		this.production = new InstantiatingHashMap<Good, AccumulatingAverage>() {

			@Override
			protected AccumulatingAverage create(Good key) {
				AccumulatingAverage ama = new AccumulatingAverage(MEMORY);
				all.add(ama);
				return ama;
			}
		};
	}

	@Override
	public void notifyProduced(IPublicCompany comp, String producer, IStock[] inputs, IStock output) {
		this.production.get(output.getGood()).add(output.getAmount());
	}

	@Override
	public void reportDividend(IPublicCompany comp, double amount) {
		this.dividends.add(amount);
	}
	
	@Override
	public void reportResults(IPublicCompany comp, double revenue, double cogs, double profits) {
		this.profits.add(profits);
	}

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
	}
	
	@Override
	public void notifySold(Good good, double quantity, Price price) {
		// System.out.println(quantity + " " + price);
		this.prices.get(good).add(quantity, price.getPrice());
	}
	
	@Override
	public void notifyTradesCancelled() {
		for (AccumulatingAverage avg : all) {
			avg.reset();
		}
	}

	@Override
	public void notifyMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(this);
	}
	
	public boolean hasJustReachedStability(){
		boolean wasStable = this.stable;
		this.stable = isStable();
		return this.stable && !wasStable;
	}

	public boolean isStable() {
		for (AccumulatingAverage ma : all) {
			if (!ma.isStable()) {
				return false;
			}
		}
		return true;
	}
	
	private double util;
	
	public double getLatestUtility(){
		return util;
	}
	
	@Override
	public void notifyDayEnded(int day, double utility) {
		this.util = utility;
		for (AccumulatingAverage avg : all) {
			avg.flush();
		}
	}

	public void printResult(PrintStream ps) {
		ps.println("Dividends " + dividends);
		ps.println("Profits " + profits);
		for (Map.Entry<Good, AccumulatingAverage> e : prices.entrySet()) {
			ps.println(e.getKey() + " price: " + e.getValue());
			if (production.containsKey(e.getKey())) {
				ps.println(e.getKey() + " production: " + production.get(e.getKey()));
			}
		}

	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		return Arrays.asList();
	}
	
	@Override
	public Collection<TimeSeries> getTimeSeries() {
		return Collections.emptyList();
	}

}
