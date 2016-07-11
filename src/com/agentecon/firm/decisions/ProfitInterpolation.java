package com.agentecon.firm.decisions;

import java.util.Random;

import com.agentecon.util.MovingCovariance;

public class ProfitInterpolation implements IFirmDecisions {

	private Random rand;
	private EExplorationMode mode;
	private MovingCovariance covariance;

	public ProfitInterpolation(double memory, EExplorationMode mode, long seed) {
		this(new MovingCovariance(memory), mode, seed);
	}

	protected ProfitInterpolation(MovingCovariance covariance, EExplorationMode mode, long seed) {
		this.mode = mode;
		this.rand = new Random(seed);
		this.covariance = covariance;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double profits = mode.selectRevenue(metrics) - mode.selectCosts(metrics);
		double size = metrics.getCash() - profits;
		covariance.add(size, profits);
		double correlation = covariance.getCorrelation();
		System.out.println(profits + "\t" + size + "\t" + correlation);
		return profits - size / 200 * (correlation + 2*(rand.nextDouble() - 0.5));
	}

	@Override
	public double calcCogs(double cash, double idealCogs) {
		return cash / 5;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new ProfitInterpolation(covariance, mode, rand.nextLong());
	}

}
