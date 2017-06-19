package com.agentecon.goods;

public class Quantity {
	
	private final Good good;
	private final double amount;
	
	public Quantity(Good good, double amount){
		this.good = good;
		this.amount = amount;
		assert this.good != null;
		assert this.amount >= 0.0;
	}

	public double getAmount() {
		return amount;
	}

	public Good getGood() {
		return good;
	}
	
}
