package com.agentecon.price;


public class ConstantFactorBelief extends AdjustableBelief {

	private double factor;
	
	public ConstantFactorBelief(double delta) {
		super();
		this.factor = 1.0 + delta;
	}
	
	@Override
	protected double getFactor(boolean increase) {
		return factor;
	}

}
