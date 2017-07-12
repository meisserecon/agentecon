/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.goods.Stock;
import com.agentecon.market.GoodStats;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.ConsumerRanking;
import com.agentecon.metric.DividendStats;
import com.agentecon.metric.SimStats;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class LocalSimulationRunner {

	private static Good potatoe = new Good("Potatoe");
	private static Good land = new Good("Land");
	private static Good manhour = new Good("Man-hour");

	public static void main(String[] args) throws SocketTimeoutException, IOException, PriceUnknownException {
		SimulationHandle handle = new LocalSimulationHandle();
		ISimulation sim = new SimulationLoader(handle).loadSimulation();
		ConsumerRanking ranking = new ConsumerRanking();
		{
			// SimStats stats = new ProductionStats();
			SimStats stats = new DividendStats(sim.getAgents());
			sim.addListener(stats);
			sim.addListener(ranking);
			sim.run();
			stats.print(System.out);
		}

		IStatistics stats = sim.getStatistics();
		GoodStats manhours = stats.getGoodsMarketStats().getStats(manhour);
		double optimalNumberOfFirms = manhours.getYesterday().getTotWeight() / 6.0 * (1 - 0.75);
		System.out.println(manhours + " implies optimal number of firms k=" + optimalNumberOfFirms);
		System.out.println(stats.getGoodsMarketStats());

		ranking.print(System.out);

		IProductionFunction prodFun = sim.getConfig().getInnovation().createProductionFunction(potatoe);
		Inventory inv = new Inventory(new Good("Gold"), new Stock(land, 100));
		double optimalCost = prodFun.getCostOfMaximumProfit(inv, stats.getGoodsMarketStats());
		double optimalManhours = optimalCost / stats.getGoodsMarketStats().getPriceBelief(manhour);
		double fixedCosts = prodFun.getFixedCost(manhour) * stats.getGoodsMarketStats().getPriceBelief(manhour);
		inv.getStock(manhour).add(optimalManhours);
		Quantity prod = prodFun.produce(inv);
		double profits = prod.getAmount() * stats.getGoodsMarketStats().getPriceBelief(potatoe) - optimalCost;
		double profits2 = (optimalCost - fixedCosts) / 3 - fixedCosts;
		System.out.println("Firm should use " + optimalManhours + " " + manhour + " to produce " + prod + " and yield a profit of " + profits + " (" + profits2 + ")");

		double totalInput = manhours.getYesterday().getTotWeight();
		double perFirm = totalInput / optimalNumberOfFirms;
		inv.getStock(manhour).add(perFirm);
		double output = prodFun.produce(inv).getAmount() * optimalNumberOfFirms;
		System.out.println("With " + optimalNumberOfFirms + " firms the " + totalInput + " manhours could have produced " + output + " instead of "
				+ stats.getGoodsMarketStats().getStats(potatoe).getYesterday().getTotWeight());

		double altInput = 12;
		System.out.println("Using only " + altInput + " potatoes would yield a profit of " + getProfits(prodFun, stats, altInput));

	}

	private static double getProfits(IProductionFunction prodFun, IStatistics sim, double inputAmount) throws PriceUnknownException {
		Inventory inv = new Inventory(new Good("Gold"), new Stock(land, 100));
		double costs = inputAmount * sim.getGoodsMarketStats().getPriceBelief(manhour);
		inv.getStock(manhour).add(inputAmount);
		Quantity prod = prodFun.produce(inv);
		return prod.getAmount() * sim.getGoodsMarketStats().getPriceBelief(potatoe) - costs;
	}

}
