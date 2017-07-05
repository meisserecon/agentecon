package com.agentecon.metric;

import java.io.PrintStream;
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
	
	public void print(PrintStream out) {
		out.println("***" + getClass().getSimpleName() + "***");
		out.print("Day");
		Collection<TimeSeries> series = getTimeSeries();
		int start = Integer.MAX_VALUE;
		int end = 0;
		for (TimeSeries ts: series){
			out.print("\t" + ts.getName());
			start = Math.min(start, ts.getStart());
			end = Math.max(end, ts.getEnd());
		}
		out.println();
		for (int day=start; day<=end; day++){
			System.out.print(day);
			for (TimeSeries ts: getTimeSeries()){
				out.print("\t" + ts.get(day));
			}
			out.println();
		}
	}
	
}
