/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.Farmer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.research.IResearchProject;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.world.ICountry;

public class GrowthConfiguration extends SimulationConfig implements IUtilityFactory, IInnovation {

	private static final int MARKET_MAKERS = 5;
	
	public static final Good LAND = HermitConfiguration.LAND;
	public static final Good POTATOE = HermitConfiguration.POTATOE;
	public static final Good MAN_HOUR = HermitConfiguration.MAN_HOUR;

	public GrowthConfiguration() {
		super(2000);
		IStock[] initialEndowment = new IStock[] { new Stock(LAND, 100), new Stock(getMoney(), 1000) };
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment farmerEndowment = new Endowment(getMoney(), initialEndowment, dailyEndowment);
		addEvent(new ConsumerEvent(30, farmerEndowment, this){
			@Override
			protected IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility util){
				return new Farmer(id, end, util);
			}
		});
		final Endowment consumerEndowment = new Endowment(getMoney(), new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT));
		addEvent(new GrowthEvent(0, 0.001) {
			
			@Override
			protected void execute(ICountry sim) {
				sim.add(new Consumer(sim.getAgents(), consumerEndowment, create(0)));
			}
		});
//		addEvent(new SimEvent(0, MARKET_MAKERS) {
//
//			@Override
//			public void execute(int day, ICountry sim) {
//				for (int i = 0; i < getCardinality(); i++) {
//					sim.add(new MarketMaker(sim, getMoney(), sim.getAgents().getFirms()));
//				}
//			}
//		});
	}

	@Override
	public CobbDouglasProductionWithFixedCost createProductionFunction(Good desiredOutput) {
		assert desiredOutput.equals(POTATOE);
		return new CobbDouglasProductionWithFixedCost(POTATOE, 1.0, FarmingConfiguration.FIXED_COSTS, new Weight(LAND, 0.25, true), new Weight(MAN_HOUR, 0.75));
	}

	@Override
	public IResearchProject createResearchProject(Good desiredOutput) {
		return null;
	}
	
	@Override
	public IUtility create(int number) {
		return new LogUtilWithFloor(new Weight(POTATOE, 1.0), new Weight(MAN_HOUR, 1.0));
	}
	
	

}
