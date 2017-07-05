/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.production.IProductionFunction;
import com.agentecon.ranking.ConsumerRanking;
import com.agentecon.research.IInnovation;
import com.agentecon.research.IResearchProject;
import com.agentecon.sim.SimulationConfig;

public class FarmingConfiguration extends SimulationConfig implements IInnovation {

	public static final String AGENT_CLASS_NAME = "com.agentecon.exercise2.Farmer";

	public static final Good GOLD = new Good("Gold", 1.0);
	public static final Good LAND = HermitConfiguration.LAND;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;

	private static final int ROUNDS = 1000;

	public static final Quantity FIXED_COSTS = HermitConfiguration.FIXED_COSTS;

	public FarmingConfiguration(int agents) throws IOException{
		this(new CompilingAgentFactory(AGENT_CLASS_NAME, new File("../exercises/src")), agents);
	}

	public FarmingConfiguration(IAgentFactory factory, int agents) {
		super(ROUNDS);
		IStock[] initialEndowment = new IStock[] { new Stock(LAND, 100), new Stock(GOLD, 1000) };
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, 24) };
		Endowment end = new Endowment(GOLD, initialEndowment, dailyEndowment);
		LogUtilWithFloor utility = new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
		addEvent(new ConsumerEvent(agents, end, utility) {
			@Override
			protected IConsumer createConsumer(Endowment end, IUtility util) {
				return factory.createConsumer(end, util);
			}
		});
	}

	// Use gold as money
	@Override
	public Good getMoney() {
		return GOLD;
	}

	@Override
	public IInnovation getInnovation() {
		return this;
	}

	@Override
	public IProductionFunction createProductionFunction(Good desiredOutput) {
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, FIXED_COSTS, new Weight(LAND, 0.25, true), new Weight(MAN_HOUR, 0.75));
	}

	@Override
	public IResearchProject createResearchProject(Good desiredOutput) {
		return null;
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		IAgentFactory defaultFactory = new CompilingAgentFactory(AGENT_CLASS_NAME, new File("../exercises/src")); // this factory loads your Farmer
		// IAgentFactory meisserFactory = new RemoteAgentFactory("meisserecon", "agentecon"); // loads the Hermit implementation from the meisserecon repository
		// IAgentFactory other = new RemoteAgentFactory("user", "repo"); // maybe you want to load agents from someone else's repository for comparison?

		// Create a multiplex factory that alternates between different factories when instantiating agents
		IAgentFactory factory = new AgentFactoryMultiplex(defaultFactory);

		FarmingConfiguration config = new FarmingConfiguration(factory, 10); // Create the configuration
		Simulation sim = new Simulation(config); // Create the simulation
		ConsumerRanking ranking = new ConsumerRanking(); // Create a ranking
		sim.addListener(ranking); // register the ranking as a listener interested in what is going on
		sim.run(); // run the simulation
		ranking.print(System.out); // print the resulting ranking
	}

}
