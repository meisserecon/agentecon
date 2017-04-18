package com.agentecon.finance;

public interface IRegister {
	
	public static final double SHARES_PER_COMPANY = 100;
	
	public Position createPosition();
	
	public double getAverageDividend();
	
}
