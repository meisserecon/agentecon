/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.ranking.ConsumerRanking;
import com.agentecon.sim.SimulationConfig;

public class AutarkicConsumerConfiguration extends SimulationConfig {

	public static final Good POTATOE = new Good("Potatoe", 0.95);
	public static final Good MAN_HOUR = new Good("Man-hour", 0.0);
	public static final Good LAND = new Good("Land", 1.0);
	
	private static final int ROUNDS = 10000;
	
	public AutarkicConsumerConfiguration(IAgentFactory factory){
		super(ROUNDS);
		IStock[] initialEndowment = new IStock[]{new Stock(LAND, 100)};
		IStock[] dailyEndowment = new IStock[]{new Stock(MAN_HOUR, 24)};
		Endowment end = new Endowment(initialEndowment, dailyEndowment);
		LogUtil utility = new LogUtil(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
		CobbDouglasProduction production = new CobbDouglasProduction(POTATOE, new Weight(LAND, 0.25, true), new Weight(MAN_HOUR, 0.75));
		addEvent(new ConsumerEvent(100, end, utility){
			@Override
			protected IConsumer createConsumer(Endowment end, IUtility util){
				return factory.createAutarkicConsumer(end, util, production);
			}
		});
	}

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		IAgentFactory factory = AgentFactoryMultiplex.createDefault();
		AutarkicConsumerConfiguration config = new AutarkicConsumerConfiguration(factory);
		Simulation sim = new Simulation(config);
		ConsumerRanking ranking = new ConsumerRanking();
		sim.addListener(ranking);
		sim.run();
		ranking.print(System.out);
	}

}
