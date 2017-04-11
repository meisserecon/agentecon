package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.agentecon.api.ISimulation;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.runner.OverallStats;

public class UtilityStats extends OverallStats {

	private int iter;
	private AveragingTimeSeries totUtil;
	private AveragingTimeSeries phase1Util;

	public UtilityStats() {
		this.iter = 0;
		this.totUtil = new AveragingTimeSeries("Overall");
		this.phase1Util = new AveragingTimeSeries("From day 250 to end");
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		this.totUtil.add(utility);
		if (day >= 250) {
			this.phase1Util.add(utility);
		}
	}

	@Override
	public void notifySimEnded(ISimulation sim) {
		this.totUtil.push(iter);
		this.phase1Util.push(iter);
		this.iter++;
	}

	@Override
	public Collection<? extends Chart> getCharts(long simId) {
		Collection<TimeSeries> list = Arrays.asList(totUtil.getTimeSeries(), phase1Util.getTimeSeries());
		Chart ch = new Chart(simId, "Average Utility", "Average daily utility per consumer in each iteration", list);
		return Collections.singleton(ch);
	}
	
	@Override
	public String toString(){
		return phase1Util.toString();
	}

	public AveragingTimeSeries getScore() {
		return phase1Util;
	}
	
	public AveragingTimeSeries getUtil(){
		return totUtil;
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.add(totUtil.getTimeSeries());
		return list;
	}
	
}
