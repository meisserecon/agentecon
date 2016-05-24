package com.agentecon.verification;

import com.agentecon.api.SimulationConfig;
import com.agentecon.sim.config.IConfiguration;

public class HeuristicsComparison implements IConfiguration {
	
	private int index;
	private double fr;
	
	public HeuristicsComparison(double fr){
		this.fr = fr;
		this.index = 0;
	}

	@Override
	public SimulationConfig createNextConfig() {
		return new ExplorationScenario(ExplorationScenario.DISCUSSED[index++], fr).createNextConfig();
	}

	@Override
	public boolean shouldTryAgain() {
		return index < ExplorationScenario.DISCUSSED.length;
	}

	@Override
	public String getComment() {
		return ExplorationScenario.DISCUSSED[Math.max(0, index - 1)].name();
	}

}
