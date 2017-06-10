package com.agentecon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.github.LocalSimulationHandle;
import com.agentecon.github.SimulationHandle;
import com.agentecon.util.LogClock;

public class SimulationStepper {

	private ISimulation simulation;
	private SimulationLoader loader;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		try (InputStream input = handle.open()) {
			this.loader = new SimulationLoader(input);
			this.simulation = loader.load();
		}
	}

	public StepGraph getData(int day, List<String> agents, String dataKey, int stepSize) throws IOException {
		assert day <= simulation.getConfig().getRounds();
		int startDay = day - stepSize;
		if (simulation.getDay() > startDay) {
			simulation = loader.load(); // reload, cannot step backwards
		}
		simulation.forwardTo(startDay);
		StepGraph graph = new StepGraph(simulation, agents);
		simulation.forwardTo(day);
		graph.fetch(dataKey);
		return graph;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		LogClock clock = new LogClock();
		LocalSimulationHandle local = new LocalSimulationHandle();
		clock.time("Created handle");
		SimulationStepper stepper = new SimulationStepper(local);
		clock.time("Created stepper");
		StepGraph graph1 = stepper.getData(100, Arrays.asList("consumers", "firms"), "utility", 10);
		clock.time("Stepped to day 100");
		StepGraph graph2 = stepper.getData(110, Arrays.asList("consumers", "firms"), "utility", 10);
		clock.time("Stepped to day 110");
		StepGraph graph3 = stepper.getData(100, Arrays.asList("consumers", "firms"), "utility", 10);
		clock.time("Stepped to day 100");
	}

}
