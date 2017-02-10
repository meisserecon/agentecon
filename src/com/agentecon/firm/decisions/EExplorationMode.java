package com.agentecon.firm.decisions;

public enum EExplorationMode {
	
	OPTIMAL_COST, OPTIMAL_COST2, PLANNED, KNOWN, PAIRED;
	
	public double selectCosts(IFinancials metrics) {
		switch (this) {
		default:
		case OPTIMAL_COST2:
		case OPTIMAL_COST:
			return metrics.getIdealCogs();
		case PLANNED:
			return metrics.getPlannedCogs();
		case KNOWN:
			return metrics.getLatestCogs();
		case PAIRED:
			return metrics.getLatestCogs();
		}
	}

	public double selectRevenue(IFinancials metrics) {
		switch (this) {
		default:
		case OPTIMAL_COST:
		case PLANNED:
		case PAIRED:
			return metrics.getExpectedRevenue();
		case OPTIMAL_COST2:
		case KNOWN:
			return metrics.getLatestRevenue();
		}
	}

}
