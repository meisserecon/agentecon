package com.agentecon.firm;

import com.agentecon.agent.IAgent;

public interface IShareholder extends IAgent {
	
	public Portfolio getPortfolio();
	
	public void managePortfolio(IStockMarket dsm);
	
}
