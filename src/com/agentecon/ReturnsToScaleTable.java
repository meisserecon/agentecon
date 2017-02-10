package com.agentecon;

import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.stats.Numbers;
import com.agentecon.verification.EquilibriumTest;
import com.agentecon.verification.PriceMetric;
import com.agentecon.verification.Result;
import com.agentecon.verification.StolperSamuelson;

/**
 * @author Luzius
 */
public class ReturnsToScaleTable {

	private PriceConfig config;

	public ReturnsToScaleTable() {
		this.config = new PriceConfig(true, true);
	}

	public String run() {
		String table = "delta\tdelta_high\talpha\tPizza Price\tPizza Amount\tFondue Price\tFondue Amount\tItalian Wage\tItalian Hours Worked\tSwiss Wage\tSwiss Hours Worked\tDeviation";
		System.out.println(table);
		for (double delta = 0.1; delta <= 1.001; delta += 0.01) {
			for (double deltaLow = 0.01; deltaLow <= 0.5001; deltaLow += 0.1) {
				for (double pref = 1.0; pref <= 5.001; pref += 0.5) {
					table = simulateAndTest(table, delta, deltaLow, pref);
				}
			}
		}
		return table;
	}

	protected String simulateAndTest(String table, double delta, double deltaLow, double pref) {
		StolperSamuelson ss = new StolperSamuelson(pref, new double[] { deltaLow, 1.0 - deltaLow }){

			@Override
			protected IFirmDecisions getDividendStrategy(double laborShare) {
				return new StrategyExploration(laborShare);
			}
			
		};
		int steps = 2000;
		Simulation sim = new Simulation(ss.createConfiguration(config, 2000, delta));
		sim.forward(steps / 2);
		PriceMetric prices = new PriceMetric(steps / 2);
		sim.addListener(prices);
		final EquilibriumTest[] tests = new EquilibriumTest[2];
		for (int i=0; i<tests.length; i++){
			tests[i] = new EquilibriumTest(i, sim.getFirms());
		}
		sim.finish();
		Result res = prices.getResult();
		String line = Numbers.toString(delta) + "\t" + Numbers.toString(delta * deltaLow) + "\t" + Numbers.toString(pref) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.PIZZA)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.PIZZA)) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.FONDUE)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.FONDUE)) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.IT_HOUR)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.IT_HOUR)) + "\t" + Numbers.toString(res.getPrice(StolperSamuelson.CH_HOUR)) + "\t" + Numbers.toString(res.getAmount(StolperSamuelson.CH_HOUR));
		double deviation = 0.0;
		for (EquilibriumTest test: tests){
			deviation = Math.max(test.getDeviation(res), deviation);
		}
		line += "\t" + String.format("%.6f", deviation);
		System.out.println(line);
		table += "\n" + line;
		return table;
	}

	public static void main(String[] args) {
		new ReturnsToScaleTable().run();
	}

}