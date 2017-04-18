// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.IStock;

public class FirmListeners extends AbstractListenerList<IFirmListener>implements IFirmListener {
	
	public void notifyProduced(String type, IStock[] inputs, IStock output) {
		notifyProduced(null, type, inputs, output);
	}

	public void reportDividend(double amount) {
		reportDividend(null, amount);
	}

	public void reportResults(double revenue, double cogs, double profits) {
		reportResults(null, revenue, cogs, profits);
	}

	@Override
	public void notifyProduced(IPublicCompany inst, String type, IStock[] inputs, IStock output) {
		for (IFirmListener l : list) {
			l.notifyProduced(inst, type, inputs, output);
		}
	}

	@Override
	public void reportDividend(IPublicCompany inst, double amount) {
		for (IFirmListener l : list) {
			l.reportDividend(inst, amount);
		}
	}

	@Override
	public void reportResults(IPublicCompany inst, double revenue, double cogs, double profits) {
		for (IFirmListener l : list) {
			l.reportResults(inst, revenue, cogs, profits);
		}
	}

}
