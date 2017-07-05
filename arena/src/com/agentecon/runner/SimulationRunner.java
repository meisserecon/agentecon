// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.agentecon.IIteratedSimulation;
import com.agentecon.ISimulation;
import com.agentecon.agent.IAgents;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.metric.Demographics;
import com.agentecon.metric.DividendStats;
import com.agentecon.metric.MarketStats;
import com.agentecon.metric.MonetaryStats;
import com.agentecon.metric.OwnershipStats;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.StockMarketStats;
import com.agentecon.metric.SubstanceStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.Correlator;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.sim.SimulationConfig;

public class SimulationRunner {

	private static final boolean INCL_VOLUME = false;

	public static final int VERSION = 9;
	public static boolean PRINT_CORRELATIONS = true;

	private ISimulation sim;
	private IIteratedSimulation iter;
	private ArrayList<SimStats> stats;
	private ArrayList<SimStats> latestRunStats;
	private ArrayList<OverallStats> overallStats;

	private ByteArrayOutputStream output;

	public SimulationRunner(ISimulation sim) {
		this.sim = sim;
		this.stats = new ArrayList<>();
		this.overallStats = new ArrayList<>();
		this.latestRunStats = new ArrayList<>();

		IAgents agents = sim.getAgents();
		if (hasIterations(sim)) {
			iter = (IIteratedSimulation) sim;
			this.stats.add(new MarketStats(INCL_VOLUME));
			// this.overallStats.add(new UtilityStats());
			// skip most stats
		} else {
			MarketStats mstats = new MarketStats(true);
			this.stats.add(mstats);
			StockMarketStats sstats = new StockMarketStats(agents);
			this.stats.add(sstats);
			this.stats.add(new SubstanceStats(agents, sstats, mstats));
			this.stats.add(new OwnershipStats(agents));
			this.stats.add(new DividendStats(agents));
			this.stats.add(new MonetaryStats(agents));
			// this.stats.add(new ProductionStats());
			// this.stats.add(new SingleFirmStats());
			// this.stats.add(new InventoryStats(sim));
			if (hasAging()) {
				this.stats.add(new Demographics(agents));
			}
		}
		this.latestRunStats.addAll(stats);
		this.stats.addAll(overallStats);
		this.output = new ByteArrayOutputStream();
	}

	private boolean hasIterations(ISimulation sim2) {
		return sim instanceof IIteratedSimulation && ((IIteratedSimulation) sim2).hasNext();
	}

	private boolean hasAging() {
		return sim.getConfig().hasAging();
	}

	public SimulationConfig getConfig() {
		return sim.getConfig();
	}

	public void run(IProgressListener periodic) throws IOException {
		final PrintStream oldOut = System.out;
		final PrintStream oldErr = System.err;
		try {
			PrintStream ps = new PrintStream(output) {

				public void println(String x) {
					super.println(x);
					oldOut.println(x);
				}

				public void println(Object x) {
					super.println(x);
					oldOut.println(x);
				}

			};
			System.setOut(ps);
			System.setErr(ps);

			ArrayList<SimStats> currentStats = stats;
			int maxIter = 25;
			while (sim != null) {
				// MarketMetrics prod = new MarketMetrics();
				// sim.addListener(prod);
				for (SimStats stat : currentStats) {
					stat.notifySimStarting(sim);
				}
				while (!sim.isFinished()) {
					sim.forwardTo(sim.getDay() + 10);
					if (periodic != null) {
						periodic.notifyProgress(sim.getDay());
					}
				}
				for (SimStats stat : currentStats) {
					stat.notifySimEnded(sim);
				}

				if (iter == null) {
					sim = null;
				} else {
					ps.println("Iteration done... " + iter.getComment());
					sim = --maxIter <= 0 ? null : iter.getNext();
					if (sim != null) {
						currentStats = new ArrayList<>();
						currentStats.add(new MarketStats(INCL_VOLUME));
						stats.addAll(currentStats);
						this.latestRunStats.clear();
						this.latestRunStats.addAll(currentStats);
					}
				}
			}
			if (PRINT_CORRELATIONS) {
				ps.println("Correlations in final iteraion:");
				Correlator c = new Correlator(getLatestRunTimeSeries());
				ps.println(c.getTopCorrelations(true));
				ps.println(c.getTopCorrelations(false));
			}
		} catch (NoSuchMethodError | AbstractMethodError e) {
			System.out.println("Aborted with error " + e);
		} finally {
			System.setOut(oldOut);
			System.setErr(oldErr);
		}
	}

	public Chart[] getCharts(String simId) {
		ArrayList<Chart> charts = new ArrayList<>();
		for (SimStats stat : stats) {
			for (Chart ch : stat.getCharts(simId)) {
				if (ch.hasContent()) {
					charts.add(ch);
				}
			}
		}
		return charts.toArray(new Chart[] {});
	}

	public ArrayList<TimeSeries> getLatestRunTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		for (SimStats stat : latestRunStats) {
			list.addAll(stat.getTimeSeries());
		}
		ArrayList<TimeSeries> filtered = new ArrayList<>();
		for (TimeSeries ts : list) {
			if (ts.isInteresting()) {
				filtered.add(ts);
			}
		}
		return filtered;
	}

	public String getSystemOutput() {
		return output.toString();
	}
	
}
