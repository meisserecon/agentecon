// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.api;

import com.agentecon.good.Good;
import com.agentecon.metric.IFirmListener;

public interface IFirm extends IAgent {
	
	public void addFirmMonitor(IFirmListener listener);
	
	public Good[] getInputs();
	
	public Good getOutput();
	
}
