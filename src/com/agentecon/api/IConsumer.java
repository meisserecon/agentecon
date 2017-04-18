// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.api;

public interface IConsumer extends IAgent {
	
	public double getTotalExperiencedUtility();
	
	public int getAge();
	
	public boolean isRetired();
	
	public void addListener(IConsumerListener listener);

}
