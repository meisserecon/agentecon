package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IPriceProvider;

public class LogProdFun extends AbstractProductionFunction {

	private static final double ADJUSTMENT = 1.0;

	public LogProdFun(Good output, Weight... weights) {
		super(output, weights);
	}

	@Override
	public double useInputs(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production += input.weight * Math.log(ADJUSTMENT + in.consume());
		}
		return production;
	}

	@Override
	public double getCostOfMaximumProfit(IPriceProvider prices) {
		double totWeight = getTotalWeight();
		double outprice = prices.getPrice(output);
		return outprice * totWeight;
	}

	@Override
	public double getExpenses(Good good, double price, double totalSpendings) {
		double offerPerWeight = totalSpendings / getTotalWeight();
		return offerPerWeight * getWeight(good) - price * ADJUSTMENT;
	}

}
