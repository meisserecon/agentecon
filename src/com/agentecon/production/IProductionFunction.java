package com.agentecon.production;

import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IPriceProvider;

public interface IProductionFunction {

	public Good[] getInput();
	
	public double[] getWeights();

	public Good getOutput();

	public double produce(Inventory inventory);
	
	public double getCostOfMaximumProfit(IPriceProvider prices);
	
	public double getExpenses(Good good, double price, double totalSpendings);
	
}
