package com.agentecon.firm.decisions;

import com.agentecon.stats.Numbers;

public class StrategyExploration implements IFirmDecisions {

	public static final int TYPES = 11;

	private double laborshare, br;
	private EExplorationMode mode;
	
	public StrategyExploration(double laborshare){
		this(laborshare, 1 - laborshare, EExplorationMode.PLANNED);
	}

	public StrategyExploration(double laborshare, double br, EExplorationMode mode) {
		this.laborshare = laborshare;
		this.mode = mode;
		this.br = br;
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
		double r = mode.selectRevenue(metrics);
		double d = br * r + calcBc() * c;
		return d;
	}

	protected double calcBc() {
		return (1-laborshare)/laborshare - br/laborshare;
	}

	public String toString() {
		return mode + " heuristic with b_R=" + Numbers.toString(br) + " and b_C=" + Numbers.toString(calcBc());
	}

}
