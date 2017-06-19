/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercises.ex1;

import com.agentecon.AgentFactory;
import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.configuration.HermitConfiguration;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.IProductionFunction;
import com.agentecon.ranking.ConsumerRanking;

/**
 * An autarkic consumer that produces its own food and does not
 * interact with others.
 */
public class Hermit extends Consumer {
	
	private Good manhours;
	private IProductionFunction prodFun;

	public Hermit(Endowment end, IUtility utility, IProductionFunction prodFun) {
		super(end, utility);
		this.prodFun = prodFun;
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}
	
	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// autarkic consumer does not trade, produces instead for himself
		produce(getInventory());
	}

	private void produce(Inventory inventory) {
		IStock currentManhours = inventory.getStock(manhours);

		// Play here. Maybe you find a better fraction than 70%?
		// getUtilityFunction().getWeights() might help you finding out
		// how the consumer weighs the utility of potatoes and of leisure
		// time (man-hours) relative to each other.
		double plannedLeisureTime = currentManhours.getAmount() * 0.5;
		
		Inventory productionInventory = inventory.hide(manhours, plannedLeisureTime);
		prodFun.produce(productionInventory);
	}
	
	@Override
	// this method is not needed and only here for better explanation what is going on
	public double consume() {
		// super class already knows how to consume, let it do the work
		return super.consume();
	}

	public static void main(String[] args) {
		// load the configuration that uses this consumer
		HermitConfiguration configuration = new HermitConfiguration(new AgentFactory());
		
		// load the simulation
		Simulation sim = new Simulation(configuration);
		
		// load and register the ranking as a listener
		ConsumerRanking ranking = new ConsumerRanking();
		sim.addListener(ranking);
		
		// run the simulation
		sim.run();
		
		// print the ranking
		ranking.print(System.out);
	}
	
}
