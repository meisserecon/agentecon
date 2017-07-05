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
import com.agentecon.market.GoodStats;
import com.agentecon.metric.ProductionStats;
import com.agentecon.metric.SimStats;

public class LocalSimulationRunner {

	public static void main(String[] args) throws SocketTimeoutException, IOException {
		SimulationHandle handle = new LocalSimulationHandle();
		ISimulation sim = new SimulationLoader(handle).loadSimulation();
		SimStats stats = new ProductionStats();
		sim.addListener(stats);
		sim.run();
		stats.print(System.out);
		GoodStats manhours = sim.getGoodsMarketStats().getStats(new Good("Man-hour"));
		System.out.println(manhours + " implies optimal number of firms k=" + manhours.getYesterday().getTotWeight() / 6.0 * (1-0.75));
	}

}
