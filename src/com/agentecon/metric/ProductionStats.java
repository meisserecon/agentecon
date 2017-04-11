// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.api.IFirm;
import com.agentecon.good.Good;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

/**
 * Compares what the firms of one type produced with what they could have produced given the input factors they acquired.
 */
public class ProductionStats extends SimStats {

	private int day;
	private InstantiatingHashMap<Good, ArrayList<FirmProductivityMonitor>> firmsByGood;

	public ProductionStats() {
		day = 0;
		firmsByGood = new InstantiatingHashMap<Good, ArrayList<FirmProductivityMonitor>>() {

			@Override
			protected ArrayList<FirmProductivityMonitor> create(Good key) {
				return new ArrayList<FirmProductivityMonitor>();
			}
		};
	}

	@Override
	public void notifyDayStarted(int day) {
		this.day = day;
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		try {
			FirmProductivityMonitor monitor = new FirmProductivityMonitor(firm) {

				@Override
				protected int getDay() {
					return day;
				}

			};
			firmsByGood.get(firm.getOutput()).add(monitor);
			firm.addFirmMonitor(monitor);
		} catch (AbstractMethodError e) {
		}
	}

	public Collection<? extends Chart> getCharts(long simId) {
		ArrayList<Chart> charts = new ArrayList<>();
		for (Map.Entry<Good, ArrayList<FirmProductivityMonitor>> e : firmsByGood.entrySet()) {
			ArrayList<FirmProductivityMonitor> list = e.getValue();
			charts.add(new Chart(simId, e.getKey() + " production", "Daily " + e.getKey() + " production by firm", list));
		}
		return charts;
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (Map.Entry<Good, ArrayList<FirmProductivityMonitor>> e : firmsByGood.entrySet()) {
			ArrayList<FirmProductivityMonitor> firmlist = e.getValue();
			list.addAll(TimeSeries.prefix(e.getKey() + " production", firmlist));
		}
		return list;
	}

}
