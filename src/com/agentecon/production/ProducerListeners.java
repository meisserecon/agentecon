// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.production;

import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.util.AbstractListenerList;

public class ProducerListeners extends AbstractListenerList<IProducerListener> implements IProducerListener {
	
	public void notifyProduced(String type, IStock[] inputs, IStock output) {
		notifyProduced(null, inputs, output);
	}

	public void reportResults(double revenue, double cogs, double profits) {
		reportResults(null, revenue, cogs, profits);
	}

	@Override
	public void notifyProduced(IProducer inst, Quantity[] inputs, Quantity output) {
		for (IProducerListener l : list) {
			l.notifyProduced(inst, inputs, output);
		}
	}
	
	@Override
	public void reportResults(IProducer inst, double revenue, double cogs, double profits) {
		for (IProducerListener l : list) {
			l.reportResults(inst, revenue, cogs, profits);
		}
	}

}
