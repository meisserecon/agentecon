package com.agentecon.sim;

import org.junit.Test;

import com.agentecon.Simulation;

public class SimulationTest extends SimulationListenerAdapter {
	
	@Test
	public void test() {
		Simulation sim = new Simulation();
		while (!sim.isFinished()) {
			sim.forward(100);
		}
	}
}
