// Created on May 28, 2015 by Luzius Meisser

package com.agentecon;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Ticker;
import com.agentecon.market.IMarket;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationConfig;


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
	
	public Collection<? extends IFirm> getListedCompanies();
	
	public IFirm getListedCompany(Ticker ticker);
	
	public void addListener(ISimulationListener listener);
	
	public void forward(int steps);
	
	public boolean isFinished();
	
}
