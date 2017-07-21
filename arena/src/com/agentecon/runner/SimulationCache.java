package com.agentecon.runner;

import java.util.ArrayList;

import com.agentecon.ISimulation;

public class SimulationCache {
	
	private SimulationLoader loader;
	private ArrayList<ISimulation> sims;

	public SimulationCache(SimulationLoader loader) {
		this.loader = loader;
		this.sims = new ArrayList<>();
	}

}
