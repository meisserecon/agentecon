package com.agentecon.firm;

import com.agentecon.sim.IAgent;

public interface IFirm extends IAgent {

	public IRegister getShareRegister();

	public Ticker getTicker();
	
	public void inherit(Position pos);
	
	public void raiseCapital(Object stockmarket);

	public void payDividends(int day);
	
	public void addFirmMonitor(IFirmListener monitor);

}
