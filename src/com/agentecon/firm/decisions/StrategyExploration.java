package com.agentecon.firm.decisions;

import com.agentecon.stats.Numbers;

public class StrategyExploration implements IFirmDecisions {

	public static final int TYPES = 11;

	private double laborshare, br;
	private EExplorationMode mode;

	public StrategyExploration(double laborshare, double fr, EExplorationMode mode) {
		this.laborshare = laborshare;
		this.mode = mode;
		this.br = fr;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new StrategyExploration(laborshare, br, mode);
	}

	public double calcCogs(double cash, double cogs) {
		return cash / 5.0;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double c = mode.selectCosts(metrics);
		double r = mode.selectRevenue(metrics, laborshare);
		double d = br * r + calcBc() * c;
		return d;
	}

	protected double calcBc() {
		return (1-laborshare)/laborshare - br/laborshare;
	}

	public String toString() {
		return mode + " exploration\t" + Numbers.toString(br) + "\t" + Numbers.toString(calcBc());
	}

}
