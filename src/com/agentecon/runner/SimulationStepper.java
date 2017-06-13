package com.agentecon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.data.AgentData;
import com.agentecon.data.TradeGraph;
import com.agentecon.util.LogClock;

public class SimulationStepper {

	private ISimulation simulation;
	private SimulationLoader loader;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		this.loader = new SimulationLoader(handle);
		this.simulation = loader.loadSimulation();
	}

	private ISimulation getSimulation(int day) throws IOException {
		assert day <= simulation.getConfig().getRounds();
		if (simulation.getDay() > day) {
			simulation = loader.loadSimulation(); // reload, cannot step backwards
		}
		simulation.forwardTo(day);
		return simulation;
	}

	public AgentData getAgentData(int day, int agentId) throws IOException {
		ISimulation sim = getSimulation(day);
		IAgent agent = sim.getAgents().getAgent(agentId);
		return new AgentData(agent);
	}

	public TradeGraph getData(int day, List<String> agents, String dataKey, int stepSize) throws IOException {
		ISimulation simulation = getSimulation(day - stepSize);
		TradeGraph graph = new TradeGraph(simulation, agents);
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
		TradeGraph graph1 = stepper.getData(100, Arrays.asList("consumers", "firms"), "utility", 10);
		clock.time("Stepped to day 100");
		TradeGraph graph2 = stepper.getData(110, Arrays.asList("consumers", "firms"), "utility", 10);
		clock.time("Stepped to day 110");
		TradeGraph graph3 = stepper.getData(100, Arrays.asList("consumers", "firms"), "utility", 10);
		clock.time("Stepped to day 100");
	}

}
