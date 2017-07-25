/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.price;

import com.agentecon.util.MovingCovariance;

public class CovarianceControl {
	
	private MovingCovariance cov;
	private IBelief belief;

	public CovarianceControl(double start, double memory){
		this.belief = new ConstantFactorBelief(start, 0.01);
		this.cov = new MovingCovariance(memory);
	}
	
	public double getCurrentInput(){
		return belief.getValue();
	}
	
	public void reportOutput(double output){
		this.cov.add(getCurrentInput(), output);
		this.belief.adapt(this.cov.getCovariance() > 0);
	}

}
