package com.agentecon.production;

import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.market.IPriceMakerMarket;

public interface IProducer extends IFirm, IPriceProvider {
	
	public Good[] getInputs();
	
	public Good getOutput();
	
	public void offer(IPriceMakerMarket market);
	
	public void addProducerMonitor(IProducerListener listener);

	public void adaptPrices();
	
	public void produce();

}
