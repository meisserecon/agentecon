// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric.series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.agentecon.json.Persistable;

public class Chart extends Persistable {

	private String parentSimulation;

	private String name;
	private String subtitle;
	private String stacking;
	private List<TimeSeriesData> data = new ArrayList<TimeSeriesData>();

	public Chart(String parentSim, String name, String subtitle, TimeSeries... series) {
		this(parentSim, name, subtitle, Arrays.asList(series));
	}

	public Chart(String parentSim, String name, String subtitle, Collection<? extends TimeSeries> series) {
		this.name = name;
		this.parentSimulation = parentSim;
		this.subtitle = subtitle;
		for (TimeSeries ts : series) {
			TimeSeries comp = ts.compact();
			if (comp.isInteresting()){
				this.data.add(comp.getRawData());
			}
		}
	}
	
	public void setStacking(String option) {
		this.stacking = option;
	}

	public String getStacking() {
		return stacking;
	}

	public String getName() {
		return name;
	}

	public String getParent() {
		return parentSimulation;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public Collection<TimeSeriesData> getData() {
		if (stacking == null) {
			Collections.sort(data, new Comparator<TimeSeriesData>() {

				@Override
				public int compare(TimeSeriesData o1, TimeSeriesData o2) {
					return -Float.compare(o1.getLastY(), o2.getLastY());
				}
			});
		} else {
			Collections.sort(data, new Comparator<TimeSeriesData>() {

				@Override
				public int compare(TimeSeriesData o1, TimeSeriesData o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		return data;
	}

	public boolean hasContent() {
		return !data.isEmpty();
	}
	
	@Override
	public String toString(){
		return name;
	}

}
