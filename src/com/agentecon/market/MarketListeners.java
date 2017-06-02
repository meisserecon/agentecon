package com.agentecon.market;

import com.agentecon.goods.Good;
import com.agentecon.util.AbstractListenerList;

public class MarketListeners extends AbstractListenerList<IMarketListener> implements IMarketListener {

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
		for (IMarketListener l: list){
			l.notifyOffered(good, quantity, price);
		}
	}
	
	@Override
	public void notifySold(Good good, double quantity, Price price) {
		for (IMarketListener l: list){
			l.notifySold(good, quantity, price);
		} 
	}

	@Override
	public void notifyTradesCancelled() {
		for (IMarketListener l: list){
			l.notifyTradesCancelled();
		} 
	}

}
