package com.agentecon.metric;

import java.util.Collection;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgents;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.sim.SimulationListenerAdapter;

public abstract class SimStats extends SimulationListenerAdapter {
	
	protected final IAgents agents;
	
	public SimStats(){
		this(null);
	}

	public SimStats(IAgents agents){
		this.agents = agents;
	}

	public abstract Collection<? extends Chart> getCharts(String simId);
	
	public void notifySimStarting(ISimulation sim) {
		sim.addListener(this);
	}
	
	public void notifySimEnded(ISimulation sim) {
	}
	
	public abstract Collection<TimeSeries> getTimeSeries();
	
}
