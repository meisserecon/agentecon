// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.production;

import com.agentecon.goods.IStock;

public interface IProducerListener {

	public void notifyProduced(IProducer inst, String producer, IStock[] inputs, IStock output);
	
	public void reportResults(IProducer inst, double revenue, double cogs, double expectedProfits);
}
