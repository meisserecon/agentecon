// Created on Jun 23, 2015 by Luzius Meisser

package com.agentecon.market;

import com.agentecon.goods.Good;

public interface IMarketListener {
	
	public void notifyOffered(Good good, double quantity, Price price);
	
	public void notifySold(Good good, double quantity, Price price);
	
	public void notifyTradesCancelled();

}
