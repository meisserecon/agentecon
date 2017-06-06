// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.agent.IAgent;

public interface IConsumer extends IAgent {
	
	public int getAge();
	
	public boolean isRetired();
	
	public void collectDailyEndowment();
	
	public double getTotalUtility();
	
	public void addListener(IConsumerListener listener);
	
}
