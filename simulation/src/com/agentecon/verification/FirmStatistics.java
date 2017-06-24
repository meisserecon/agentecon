package com.agentecon.verification;

import java.util.Map;

import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.AccumulatingAverage;
import com.agentecon.util.InstantiatingHashMap;

public class FirmStatistics extends SimulationListenerAdapter {
	
	private InstantiatingHashMap<String, AccumulatingAverage> avg;
	
	public FirmStatistics(){
		this.avg = new InstantiatingHashMap<String, AccumulatingAverage>() {
			
			@Override
			protected AccumulatingAverage create(String key) {
				return new AccumulatingAverage();
			}
		};
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		for (AccumulatingAverage a: avg.values()){
			a.flush();
		}
	}

	public void reportProfits(IFirmDecisions strategy, double amount) {
		avg.get(strategy.getClass().getSimpleName()).add(amount);
	}
	
	public String getRanking(){
		String ranking = null;
		for (Map.Entry<String, AccumulatingAverage> e: avg.entrySet()){
			String line = e.getKey() + " produced " + e.getValue().getWrapped();
			if (ranking == null){
				ranking = line;
			} else {
				ranking = ranking + "\n" + line;
			}
		}
		return ranking;
	}
	
	@Override
	public String toString(){
		return avg.keySet().toString();
	}

}
