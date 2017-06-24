package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.metric.series.Line;
import com.agentecon.metric.series.TimeSeries;

public class AveragingTimeSeries {

	private TimeSeries series;
	private double tot, weight;

	public AveragingTimeSeries(String key) {
		this.series = new TimeSeries(key);
	}
	
	public AveragingTimeSeries(String key, Line line) {
		this.series = new TimeSeries(key, line);
	}

	public void add(double delta) {
		this.tot += delta;
		this.weight += 1.0;
	}
	
	public void pushSum(int day){
		this.series.set(day, tot);
		reset();
	}

	public double push(int day) {
		if (weight > 0) {
			double value = tot / weight;
			this.series.set(day, value);
			reset();
			return value;
		} else {
			return 0.0;
		}
	}

	protected void reset() {
		this.weight = 0.0;
		this.tot = 0.0;
	}

	public TimeSeries getTimeSeries() {
		return series;
	}
	
	public String toString(){
		return series.toString();
	}
	
	public double getCurrent() {
		return weight == 0.0 ? 0.0 : tot / weight;
	}

	public static ArrayList<TimeSeries> unwrap(Collection<AveragingTimeSeries> values) {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (AveragingTimeSeries ats: values){
			list.add(ats.getTimeSeries());
		}
		return list;
	}

}
