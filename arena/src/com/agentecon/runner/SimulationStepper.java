package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.metric.minichart.MiniChart;
import com.agentecon.util.LogClock;
import com.agentecon.web.SoftCache;
import com.agentecon.web.methods.Rank;
import com.agentecon.web.methods.UtilityRanking;

public class SimulationStepper {

	private AtomicReference<ISimulation> simulation;
	private AtomicReference<SimulationLoader> loader;
	private AtomicReference<SoftCache<Object, Object>> cachedData;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		this.loader = new AtomicReference<SimulationLoader>(new SimulationLoader(handle));
		this.simulation = new AtomicReference<ISimulation>(loader.get().loadSimulation());
		this.cachedData = new AtomicReference<SoftCache<Object, Object>>(refreshCache());
//		this.enablePeriodicUpdate();
	}

	private SoftCache<Object, Object> refreshCache() throws IOException {
		SoftCache<Object, Object> cache = new SoftCache<>();
		cache.put(UtilityRanking.class, createRanking(loader.get().loadSimulation()));
		return cache;
	}
	
	public ISimulation getNewSimulationInstance() throws IOException{
		return this.loader.get().loadSimulation();
	}
	
	public ISimulation getSimulation() throws IOException {
		return this.simulation.get();
	}

	public ISimulation getSimulation(int day) throws IOException {
		ISimulation simulation = this.simulation.get();
		assert day <= simulation.getConfig().getRounds();
		if (simulation.getDay() > day) {
			ISimulation newSimulation = loader.get().loadSimulation(); // reload, cannot step backwards
			this.simulation.compareAndSet(simulation, newSimulation);
			simulation = newSimulation;
		}
		simulation.forwardTo(day);
		return simulation;
	}

	public void enablePeriodicUpdate() {
		Thread t = new Thread() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(60000);
						try {
							refreshSimulation();
						} catch (SocketTimeoutException e) {
							// try again in a minute
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		};
		t.setDaemon(true);
		t.start();

	}

	private void refreshSimulation() throws SocketTimeoutException, IOException {
		SimulationLoader loader = SimulationStepper.this.loader.get();
		boolean[] changed = new boolean[] { false };
		loader = loader.refresh(changed);
		if (changed[0]) {
			loader.loadSimulation();
			this.loader.set(loader);
			ISimulation prevSim = this.simulation.get();
			ISimulation newSimulation = loader.loadSimulation();
			newSimulation.forwardTo(prevSim.getDay());
			this.simulation.compareAndSet(prevSim, newSimulation);
			this.cachedData = new AtomicReference<SoftCache<Object, Object>>(refreshCache());
		}
	}

	private UtilityRanking createRanking(ISimulation sim) {
		UtilityRanking ranking = new UtilityRanking();
		sim.addListener(ranking);
		sim.run();
		return ranking;
	}
	
	public Collection<Rank> getRanking(){
		UtilityRanking ranking = (UtilityRanking) cachedData.get().get(UtilityRanking.class);
		return ranking.getRanking();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		LogClock clock = new LogClock();
//		SimulationHandle local = new GitSimulationHandle("meisserecon", "agentecon", "master");
		SimulationHandle local = new LocalSimulationHandle();
		clock.time("Created handle");
		SimulationStepper stepper = new SimulationStepper(local);
		stepper.getSimulation(100);
		stepper.getSimulation(50);
	}

	public Object getCachedItem(Object key) {
		return cachedData.get().get(key);
	}

	public void putCached(Object string, Object chart) {
		cachedData.get().put(string, chart);
	}

}
