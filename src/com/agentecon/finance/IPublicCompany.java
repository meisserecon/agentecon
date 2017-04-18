package com.agentecon.finance;

import com.agentecon.api.IAgent;
import com.agentecon.metric.IFirmListener;

public interface IPublicCompany extends IAgent {

	public IRegister getShareRegister();

	public Ticker getTicker();
	
	public void inherit(Position pos);
	
	public void raiseCapital(Object stockmarket);

	public void payDividends(int day);
	
	public void addFirmMonitor(IFirmListener monitor);

}
