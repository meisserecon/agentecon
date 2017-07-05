/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise2;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.IAgentFactory;
import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.configuration.FarmingConfiguration;
import com.agentecon.configuration.HermitConfiguration;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.IProductionFunction;
import com.agentecon.ranking.ConsumerRanking;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class Farmer extends Consumer implements IFounder {

	private static final double MINIMUM_WORKING_HOURS = 5;
	
	private Good manhours;

	public Farmer(Endowment end, IUtility utility) {
		super(end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}

	@Override
	public IFirm considerCreatingFirm(IInnovation research) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.getAmount() >= 100.0 && getAgentId() > 5) {
			// I have plenty of land, lets create a new farm with me as owner
			IShareholder owner = Farmer.this;
			IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);

			IStock wallet = getMoney();
			IStock firmMoney = wallet.hideRelative(0.5);

			AdaptiveFarm farm = new AdaptiveFarm(owner, firmMoney, myLand, prod);
			farm.getInventory().getStock(manhours).transfer(getStock(manhours), 10);
			return farm;
		} else {
			return null;
		}
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// In the beginning, shelves can be empty and thus there is no incentive to work (sell man-hours) either.
		// To kick-start the economy, we require the farmer to sell some of his man-hours anyway, even if he cannot
		// buy anything with the earned money.
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		
		// After having worked the minimum amount, work some more and buy goods for consumption in an optimal balance.
		super.tradeGoods(market);
	}

	@Override
	public double consume() {
		return super.consume();
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		FarmingConfiguration config = new FarmingConfiguration(new IAgentFactory() {

			@Override
			public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
				return new Farmer(endowment, utilityFunction);
			}
		}, 10); // Create the configuration
		Simulation sim = new Simulation(config); // Create the simulation
		ConsumerRanking ranking = new ConsumerRanking(); // Create a ranking
		sim.addListener(ranking); // register the ranking as a listener interested in what is going on
		while (!sim.isFinished()){
			sim.forwardTo(sim.getDay() + 1);
			System.out.println("Market stats at end of day " + sim.getDay());
			sim.getGoodsMarketStats().print(System.out);
		}
		ranking.print(System.out); // print the resulting ranking
	}

}
