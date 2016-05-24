package com.agentecon.verification;

import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.util.Average;

class UtilityStats extends SimulationListenerAdapter {

	private Average utility = new Average();
	private int startDay;
	
	public UtilityStats(int startDay){
		this.startDay = startDay;
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		if (day >= startDay) {
			this.utility.add(utility);
		}
	}

	public double getUtility() {
		return utility.getAverage();
	}

}