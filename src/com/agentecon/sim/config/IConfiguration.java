package com.agentecon.sim.config;

import com.agentecon.sim.SimulationConfig;

public interface IConfiguration {

	public SimulationConfig createNextConfig();

	public boolean shouldTryAgain();

	public String getComment();

}
