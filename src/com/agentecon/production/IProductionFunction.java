package com.agentecon.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IPriceProvider;

public interface IProductionFunction {

	/**
	 * Returns the input goods for this production function, including
	 * both transformed and capital goods.
	 * 
	 * Use 'getWeights' to find out which inputs are capital goods.
	 */
	public Good[] getInputs();
	
	/**
	 * Returns the relative weights of the different input goods and
	 * whether they are capital goods or transformed goods.
	 */
	public Weight[] getWeights();

	/**
	 * Returns the output produced by this production function.
	 */
	public Good getOutput();

	/**
	 * The production function is applied to the goods found in the inventory
	 * and resulting output added to the inventory.
	 * 
	 * Capital might be needed, but is not depleted.
	 * Other inputs are fully depleted. If you do not want to fully use the inputs,
	 * use inventory.hide to create a delegate inventory that hides some of its
	 * goods.
	 * 
	 * This function must only be called once per day!
	 * 
	 * @return the amount of the output produced
	 */
	public double produce(Inventory inventory);
	
	/**
	 * Returns how much should optimally be spent on the input goods given the provided
	 * price beliefs.
	 * Capital inputs are not taken into account.
	 */
	public double getCostOfMaximumProfit(IPriceProvider prices);
	
	/**
	 * Tells how how much to optimally spend on each input good given total spendings.
	 * Capital inputs are not taken into account.
	 */
	public double getExpenses(Good good, double price, double totalSpendings);
	
}
