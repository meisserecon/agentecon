package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.api.IAgent;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.IStock;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.stats.Numbers;
import com.agentecon.util.InstantiatingHashMap;

public class DividendStats extends SimStats {

	private HashMap<String, AveragingTimeSeries> dividends;

	public DividendStats() {
		this.dividends = new InstantiatingHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key);
			}
		};
	}

	@Override
	public void notifyAgentCreated(IAgent firm) {
		if (firm instanceof IPublicCompany) {
			((IPublicCompany)firm).addFirmMonitor(new IFirmListener() {
				
				@Override
				public void reportResults(IPublicCompany comp, double revenue, double cogs, double expectedProfits) {
				}
				
				@Override
				public void reportDividend(IPublicCompany comp, double amount) {
					if (comp == null){
						DividendStats.this.reportDividend(firm.getType(), amount);
					} else {
						DividendStats.this.reportDividend(comp.getType(), amount);
					}
				}
				
				@Override
				public void notifyProduced(IPublicCompany comp, String producer, IStock[] inputs, IStock output) {
				}
			});
		}
	}
	
	protected void reportDividend(String type, double amount) {
		dividends.get(type).add(amount);
	}

	@Override
	public void notifyDayEnded(int day) {
		String line = day + " dividends";
		for (AveragingTimeSeries ts: dividends.values()){
			double value = ts.push(day);
			line += ", " + ts.getTimeSeries().getName() + " " + Numbers.toString(value);
		}
//		System.out.println(line);
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		return Collections.singleton(new Chart(simId, "Dividends", "Average daily dividend per firm type", AveragingTimeSeries.unwrap(dividends.values())));
	}

	@Override
	public ArrayList<TimeSeries> getTimeSeries() {
		return TimeSeries.prefix("Dividends", AveragingTimeSeries.unwrap(dividends.values()));
	}

}
