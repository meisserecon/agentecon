// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;

public interface IConsumer extends IAgent {
	
	/**
	 * Called every morning.
	 */
	public void collectDailyEndowment();

	/**
	 * Buy and sell goods on the market in a hopefully optimal way given the
	 * offers provided by the market makers of the goods market.
	 * This method is invoked on all IConsumers before consumption.
	 */
	public void tradeGoods(IPriceTakerMarket market);
	
	/**
	 * Time to consume, called once per day in the evening after trading goods.
	 * @return the utility gained from consumption.
	 */
	public double consume();

	/**
	 * Get one day older and die if the maximum age is reached.
	 * In case of death, the inventory must be returned and the remaining
	 * portfolio transferred to the 'inheritance' portfolio.
	 */
	public Inventory age(Portfolio inheritance);
	
	public int getAge();
	
	public boolean isRetired();
	
	public IUtility getUtilityFunction();
	
	public void addListener(IConsumerListener listener);
	
}
