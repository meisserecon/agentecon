/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm.decisions;

import com.agentecon.util.MovingCovariance;

/**
 * Responsible for financial planning and steering
 * the firms size by adjusting its leverage and its
 * dividends.
 */
public class FinanceDepartment {
	
	private MovingCovariance cov;
	
	public FinanceDepartment(){
		
	}

	public double calculateDividends(double profits, double size) {
//		double correlation = covariance.getCorrelation();
//		System.out.println(profits + "\t" + size + "\t" + correlation);
//		return profits - size / 200 * (correlation + 0*(rand.nextDouble() - 0.5));
		return 0.0;
	}

}
