package com.agentecon.production;

import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.market.IPriceMakerMarket;

public interface IProducer extends IFirm {
	
	public Good[] getInputs();
	
	public Good getOutput();
	
	public void offer(IPriceMakerMarket market);
	
	public void produce();
	
	public void notifyMarketClosed();
	
	public void addProducerMonitor(IProducerListener listener);

}
