// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.api.Price;
import com.agentecon.good.Good;

public interface IMarketListener {
	
	public void notifyOffered(Good good, double quantity, Price price);
	
	public void notifySold(Good good, double quantity, Price price);
	
	public void notifyTradesCancelled();

}
