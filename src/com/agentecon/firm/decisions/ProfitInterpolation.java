package com.agentecon.firm.decisions;

public class ProfitInterpolation implements IFirmDecisions {

	private int index;
	private double[] size;
	private double[] profits;
	private double aggressiveness;
	private EExplorationMode mode;

	public ProfitInterpolation(int memory, double aggressiveness, EExplorationMode mode) {
		this.index = 0;
		this.mode = mode;
		this.size = new double[memory];
		this.profits = new double[memory];
		this.aggressiveness = aggressiveness;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double profits = mode.selectRevenue(metrics) - mode.selectCosts(metrics);
		double size = metrics.getCash() - profits;
		report(size, profits);
		double correlation = correlate();
		return profits - aggressiveness * correlation;
	}

	private double correlate() {
		double avgSize = avg(size);
		double avgProf = avg(profits);
		double varSize = var(size, avgSize);
		double varProf = var(profits, avgProf);
		double sum = 0.0;
		for (int i = 0; i < size.length; i++) {
			sum += (profits[i] - avgProf) * (size[i] - avgSize);
		}
		double cov = sum / size.length;
		if (varProf == 0.0 || varSize == 0.0) {
			return 0.0;
		} else {
			return cov / Math.sqrt(varProf) / Math.sqrt(varSize);
		}
	}

	private double var(double[] arr, double avg) {
		double sum = 0.0;
		for (double s : arr) {
			sum += (s - avg) * (s - avg);
		}
		return sum / arr.length;
	}

	private double avg(double[] size) {
		double sum = 0.0;
		for (double s : size) {
			sum += s;
		}
		return sum / size.length;
	}

	private void report(double size, double profits) {
		int len = this.size.length;
		this.size[index] = size;
		this.profits[index] = profits;
		this.index = (index + 1) % len;
	}

	@Override
	public double calcCogs(double cash, double idealCogs) {
		return cash / 5;
	}

	@Override
	public IFirmDecisions duplicate() {
		return new ProfitInterpolation(size.length, aggressiveness, mode);
	}

}
