package com.agentecon.firm;

import com.agentecon.sim.IAgent;

public interface IShareholder extends IAgent {
	
	public Portfolio getPortfolio();
	
	public void managePortfolio(IStockMarket dsm);
	
}
