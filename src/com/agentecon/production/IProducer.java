package com.agentecon.production;

import com.agentecon.api.IFirm;
import com.agentecon.market.IPriceMakerMarket;

public interface IProducer extends IFirm {
	
	public void offer(IPriceMakerMarket market);
	
	public double produce(int day);
	
	public void notifyMarketClosed();

}
