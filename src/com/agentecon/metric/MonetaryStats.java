package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.Line;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

public class MonetaryStats extends SimStats {

	private static final int SKIP = 10;

	private ISimulation sim;
	private HashMap<String, AveragingTimeSeries> cashByType;

	public MonetaryStats(ISimulation world) {
		this.sim = world;
		this.cashByType = new InstantiatingHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key, new Line());
			}
		};
	}

	@Override
	public void notifyDayEnded(int day) {
		try {
			for (IAgent a : sim.getAgents()) {
				double money = a.getMoney().getAmount();
				cashByType.get(a.getType()).add(money / SKIP);
			}
			if (day % SKIP == 0) {
				for (AveragingTimeSeries ats : cashByType.values()) {
					ats.pushSum(day);
				}
			}
		} catch (AbstractMethodError e) {
		}
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		Chart ch = new Chart(simId, "Cash", "Overnight cash holdings by agent type", AveragingTimeSeries.unwrap(cashByType.values()));
		ch.setStacking("normal");
		return Collections.singleton(ch);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Overnight cash", AveragingTimeSeries.unwrap(cashByType.values())));
		return list;
	}

}
