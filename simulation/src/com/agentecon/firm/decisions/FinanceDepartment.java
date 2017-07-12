/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm.decisions;

import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.MovingCovariance;

/**
 * Responsible for financial planning and steering
 * the firms size by adjusting its leverage and its
 * dividends.
 */
public class FinanceDepartment {
	
	private MovingCovariance cov;
	private IFinancials financials;
	private ExpectedRevenueBasedStrategy strat;
	
	public FinanceDepartment(IFinancials financials){
		this.financials = financials;
		this.cov = new MovingCovariance(0.98);
		this.strat = new ExpectedRevenueBasedStrategy(0.75);
	}

	public double calculateDividends() throws PriceUnknownException {
		double profits = financials.getLatestRevenue() - financials.getLatestCogs();
		double size = financials.getCash();
		this.cov.add(size, profits);
		
//		double targetSize = calculateTargetSize(size);
		double targetSize = financials.getIdealCogs() * 5;
		// once ideal size is reached, all profits are distributed
		double dividend = profits + size - targetSize; // once ideal size is reached
//		System.out.println("Size " + size + " target " + targetSize + ", dividend: " + dividend);
		return strat.calcDividend(financials) - 100;
	}
	
	private double calculateTargetSize(double currentSize){
		double correlation = cov.getCorrelation(); // between -1 and 1
		double adjustmentFactor = 1 + Math.abs(correlation/10);
		if (correlation > 0){
			return currentSize * adjustmentFactor; // grow
		} else {
			return currentSize / adjustmentFactor; // shrink
		}
	}

}
