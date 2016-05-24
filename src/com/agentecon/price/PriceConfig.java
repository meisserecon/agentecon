package com.agentecon.price;

import java.util.Random;

public class PriceConfig {

	private static final double DEFAULT_ACCURACY = 0.03;

	private boolean sensor;
	private double accuracy;
	private boolean exp;

	public PriceConfig(boolean exp, boolean sensor) {
		this(exp, sensor, DEFAULT_ACCURACY);
	}

	public PriceConfig(boolean exp, boolean sensor, double accuracy) {
		super();
		this.exp = exp;
		this.sensor = sensor;
		this.accuracy = accuracy;
	}

	public IPrice createPrice(Random rand) {
		if (exp){
			return new ExpSearchPrice(accuracy);
		} else {
			return new RandomizedFactorPrice(rand, accuracy);
		}
	}

	public boolean isSensor() {
		return sensor;
	}

	public String getName() {
		if (sensor) {
			return "exp search price with sensor";
		} else {
			return "exp search price";
		}
	}

	public String toString() {
		return getName();
	}

}
