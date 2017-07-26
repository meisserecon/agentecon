/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.learning;

import java.util.Random;

public class QuadraticMaximizer extends RecursiveLeastSquares {

	private Random rand;
	private double latest;
	private double min, max;

	public QuadraticMaximizer(double memory, int parameters, long randSeed, double min, double max) {
		super(memory, parameters);
		this.rand = new Random(randSeed);
		this.min = min;
		this.max = max;
		this.latest = 0.0;
	}
	
	public double getOptimalInput() {
		double a = weights.get(0, 2);
		double b = weights.get(0, 1);
		double optimum = -b / (2 * a); // extremum for function of form a*x*x + b*x + c
		if (min <= optimum && optimum <= max) {
			// Adding random noise helps against getting stuck
			this.latest = optimum + (rand.nextDouble() - 0.5)/50;
		} else {
			this.latest = rand.nextDouble() * (max - min) + min;
		}
		return this.latest;
	}
	
	public void update(double result){
		super.update(new Matrix(1.0, latest, latest*latest), result);
	}

}
