package com.agentecon.firm;

import com.agentecon.agent.IAgent;

public interface IFirm extends IAgent {

	public IRegister getShareRegister();

	public Ticker getTicker();
	
	public void inherit(Position pos);
	
	public void raiseCapital(IStockMarket stockmarket);

	public void payDividends(int day);
	
	public void addFirmMonitor(IFirmListener monitor);

}
