// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.api.IFirm;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.IStock;
import com.agentecon.metric.series.TimeSeries;

public abstract class FirmProductivityMonitor extends TimeSeries implements IFirmListener {
	
	public FirmProductivityMonitor(IFirm firm){
		super(firm.getName());
	}
	
	@Override
	public void reportResults(IPublicCompany comp, double revenue, double cogs, double profits) {
	}
	
	@Override
	public void reportDividend(IPublicCompany comp, double amount) {
	}
	
	@Override
	public void notifyProduced(IPublicCompany comp, String type, IStock[] inputs, IStock output) {
		super.set(getDay(), output.getAmount());
	}

	protected abstract int getDay();
	
}
