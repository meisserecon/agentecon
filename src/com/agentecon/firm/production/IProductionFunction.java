package com.agentecon.firm.production;

import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;

public interface IProductionFunction {

	public Good[] getInput();
	
	public double[] getWeights();

	public Good getOutput();

	public double produce(Inventory inventory);
	
	public double getCostOfMaximumProfit(IPriceProvider prices);
	
	public double getExpenses(Good good, double price, double totalSpendings);
	
//	public double getMaximumProfits(IPriceProvider prices);

}
