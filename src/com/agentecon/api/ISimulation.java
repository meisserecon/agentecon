// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.api;

import java.util.Collection;

import com.agentecon.finance.IPublicCompany;
import com.agentecon.finance.IShareholder;
import com.agentecon.finance.Ticker;
import com.agentecon.metric.ISimulationListener;


/**
 * The interface
 * 
 */
public interface ISimulation {
	
	public int getDay();
	
	public String getName();
	
	public String getDescription();
	
	public SimulationConfig getConfig();
	
	public IMarket getStockMarket();
	
	public Collection<? extends IAgent> getAgents();
	
	public Collection<? extends IConsumer> getConsumers();
	
	public Collection<? extends IFirm> getFirms();
	
	public Collection<? extends IShareholder> getShareHolders();
	
	public Collection<? extends IPublicCompany> getListedCompanies();
	
	public IPublicCompany getListedCompany(Ticker ticker);
	
	public void addListener(ISimulationListener listener);
	
	public void forward(int steps);
	
	public boolean isFinished();
	
}
