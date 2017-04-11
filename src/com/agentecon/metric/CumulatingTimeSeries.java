package com.agentecon.metric;

import com.agentecon.metric.series.TimeSeries;

public class CumulatingTimeSeries {

	private TimeSeries series;
	private double population;

	public CumulatingTimeSeries(String key) {
		this.series = new TimeSeries(key);
	}

	public void update(double delta) {
		this.population += delta;
	}

	public void push(int day) {
		this.series.set(day, population);
	}

	public TimeSeries getTimeSeries() {
		return series;
	}

}
