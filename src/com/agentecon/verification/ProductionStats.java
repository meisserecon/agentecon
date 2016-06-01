package com.agentecon.verification;

import com.agentecon.api.IFirm;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.AccumulatingAverage;

class ProductionStats extends SimulationListenerAdapter implements IFirmListener {

	private AccumulatingAverage inputsUsed;
	private int startDay;

	public ProductionStats(int startDay) {
		this.startDay = startDay;
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		firm.addFirmMonitor(this);
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		if (day == startDay - 1) {
			inputsUsed = new AccumulatingAverage();
		} else if (inputsUsed != null) {
			inputsUsed.flush();
		}
	}

	public double getInput1Volume() {
		return inputsUsed.getWrapped().getAverage();
	}

	@Override
	public void notifyProduced(IPublicCompany inst, String producer, IStock[] inputs, IStock output) {
		if (inputsUsed != null) {
			inputsUsed.add(inputs[0].getAmount());
		}
	}

	@Override
	public void reportDividend(IPublicCompany inst, double amount) {
	}

	@Override
	public void reportResults(IPublicCompany inst, double revenue, double cogs, double expectedProfits) {
	}

}