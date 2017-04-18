// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.metric;

import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.IStock;

public interface IFirmListener {

	public void notifyProduced(IPublicCompany inst, String producer, IStock[] inputs, IStock output);
	
	public void reportDividend(IPublicCompany inst, double amount);
	
	public void reportResults(IPublicCompany inst, double revenue, double cogs, double expectedProfits);

}
