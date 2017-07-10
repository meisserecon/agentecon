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
import com.agentecon.metric.ProductionStats;
import com.agentecon.metric.SimStats;
import com.agentecon.production.IProductionFunction;

public class LocalSimulationRunner {

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		SimulationHandle handle = new LocalSimulationHandle();
		ISimulation sim = new SimulationLoader(handle).loadSimulation();
		SimStats stats = new ProductionStats();
//		SimStats stats = new DividendStats(sim.getAgents());
		sim.addListener(stats);
		sim.run();
		stats.print(System.out);
		Good manhour = new Good("Man-hour");
		GoodStats manhours = sim.getGoodsMarketStats().getStats(manhour);
		System.out.println(manhours + " implies optimal number of firms k=" + manhours.getYesterday().getTotWeight() / 6.0 * (1-0.75));
		System.out.println(sim.getGoodsMarketStats());
		
		Good potatoe = new Good("Potatoe");
		Good land = new Good("Land");
		IProductionFunction prodFun = sim.getConfig().getInnovation().createProductionFunction(potatoe);
		Inventory inv = new Inventory(new Good("Gold"), new Stock(land, 100));
		double optimalCost = prodFun.getCostOfMaximumProfit(inv, sim.getGoodsMarketStats());
		double optimalManhours = optimalCost / sim.getGoodsMarketStats().getPriceBelief(manhour);
		inv.getStock(manhour).add(optimalManhours);
		Quantity prod = prodFun.produce(inv);
		System.out.println("Firm should use " + optimalManhours + " " + manhour + " to produce " + prod);
	}

}
