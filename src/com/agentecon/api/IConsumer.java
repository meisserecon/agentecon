// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.api;

import com.agentecon.finance.IStockMarketParticipant;

public interface IConsumer extends IAgent, IStockMarketParticipant {
	
	public int getAge();
	
	public boolean isRetired();
	
	public void collectDailyEndowment();
	
	public void addListener(IConsumerListener listener);

}
