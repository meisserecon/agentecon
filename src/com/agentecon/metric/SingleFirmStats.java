package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.agentecon.api.IFirm;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.IStock;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;

public class SingleFirmStats extends SimStats {

	private int day;
	private HashMap<String, FirmTimeSeries> data;

	public SingleFirmStats() {
		this.data = new HashMap<>();
	}

	@Override
	public void notifyDayStarted(int day) {
		this.day = day;
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		if (data.containsKey(firm.getType())) {
			// skip
		} else {
			data.put(firm.getType(), new FirmTimeSeries(firm));
		}
	}

	@Override
	public Collection<? extends Chart> getCharts(long simId) {
		ArrayList<Chart> charts = new ArrayList<>();
		for (FirmTimeSeries ts: data.values()){
			charts.add(ts.createChart(simId));
		}
		return charts;
	}

	class FirmTimeSeries implements IFirmListener {

		private String name;
		private TimeSeries dividends, cogs, revenue, cash;

		public FirmTimeSeries(IFirm firm) {
			this.name = firm.getName();
			this.dividends = new TimeSeries("Dividends");
			this.cogs = new TimeSeries("Cost of goods sold");
			this.revenue = new TimeSeries("Revenue");
			this.cash = new TimeSeries("Cash");
			firm.addFirmMonitor(this);
		}

		public Chart createChart(long simId) {
			return new Chart(simId, name, "Results of a single firm", Arrays.asList(revenue, cogs, cash, dividends));
		}

		@Override
		public void notifyProduced(IPublicCompany comp, String producer, IStock[] inputs, IStock output) {
		}

		@Override
		public void reportDividend(IPublicCompany comp, double amount) {
			this.dividends.set(day, amount);
			this.cash.set(day, comp.getMoney().getAmount());
		}

		@Override
		public void reportResults(IPublicCompany comp, double revenue, double cogs, double profits) {
			this.cogs.set(day, cogs);
			this.revenue.set(day, revenue);
		}
	}
	

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		return list;
	}

}
