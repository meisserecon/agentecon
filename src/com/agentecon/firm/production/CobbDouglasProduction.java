package com.agentecon.firm.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IPriceProvider;

public class CobbDouglasProduction extends AbstractProductionFunction {

	public static final double PRODUCTIVITY = 10;
	
	private double constantFactor;

	public CobbDouglasProduction(Good output, Weight... weights) {
		this(output, PRODUCTIVITY, weights);
	}

	public CobbDouglasProduction(Good output, double constantFactor, Weight... weights) {
		super(output, weights);
		this.constantFactor = constantFactor;
	}

	public CobbDouglasProduction scale(double returnsToScale) {
		double current = getReturnsToScale();
		double factor = returnsToScale / current;
		Weight[] newWeights = new Weight[inputs.length];
		for (int i = 0; i < newWeights.length; i++) {
			newWeights[i] = new Weight(inputs[i].good, inputs[i].weight * factor);
		}
		return new CobbDouglasProduction(getOutput(), constantFactor, newWeights);
	}

	public double getReturnsToScale() {
		return super.getTotalWeight();
	}

	@Override
	public double useInputs(Inventory inventory) {
		double production = 1.0;
		for (Weight input : inputs) {
			IStock in = inventory.getStock(input.good);
			production *= Math.pow(input.capital ? in.getAmount() : in.consume(), input.weight);
		}
		production = Math.max(constantFactor * production, 1.0);
		return production;
	}
	
	@Override
	public double getCostOfMaximumProfit(IPriceProvider prices) {
		double totWeight = getTotalWeight();
		if (totWeight >= 1.0) {
			// increasing returns to scale
			return Double.MAX_VALUE;
		} else {
			double outprice = prices.getPrice(output);
			double prod = getCBHelperProduct(prices);
			double factor = Math.pow(outprice * prod, 1 / (1 - totWeight));
			return totWeight * factor;
		}
	}

	private double getCBHelperProduct(IPriceProvider prices) {
		double tot = constantFactor;
		for (Weight in : inputs) {
			double price = prices.getPrice(in.good);
			if (Double.isInfinite(price)) {
				// skip, not obtainable
			} else {
				tot *= Math.pow(in.weight / price, in.weight);
			}
		}
		return tot;
	}

	@Override
	public double getExpenses(Good good, double price, double totalSpendings) {
		double offerPerWeight = totalSpendings / getTotalWeight();
		return offerPerWeight * getWeight(good);
	}

}
