package com.agentecon.runner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.data.AgentQuery;
import com.agentecon.data.JsonData;
import com.agentecon.data.TradeGraph;
import com.agentecon.util.LogClock;

public class SimulationStepper {
	
	private ISimulation simulation;
	private SimulationLoader loader;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		this.loader = new SimulationLoader(handle);
		this.simulation = loader.loadSimulation();
	}

	public ISimulation getSimulation(int day) throws IOException {
		assert day <= simulation.getConfig().getRounds();
		if (simulation.getDay() > day) {
			simulation = loader.loadSimulation(); // reload, cannot step backwards
		}
		simulation.forwardTo(day);
		return simulation;
	}
	
//	public static void main(String[] args) throws IOException, InterruptedException {
//		LogClock clock = new LogClock();
//		LocalSimulationHandle local = new LocalSimulationHandle();
//		clock.time("Created handle");
//		SimulationStepper stepper = new SimulationStepper(local);
//		clock.time("Created stepper");
//		JsonData graph1 = stepper.getData(100, Arrays.asList("consumers", "firms"), 10);
//		clock.time("Stepped to day 100");
//		JsonData graph2 = stepper.getData(110, Arrays.asList("consumers", "firms"), 10);
//		JsonData agentData = stepper.getAgentData(200, "firms");
//		System.out.println("Agent 13: " + agentData.getJson());
//		clock.time("Stepped to day 110");
//		JsonData graph3 = stepper.getData(100, Arrays.asList("consumers", "firms"), 10);
//		clock.time("Stepped to day 100");
//	}

}
