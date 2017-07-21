package com.agentecon.runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.agentecon.ISimulation;

public class SimulationCache {

	private static final int MAX_ENTRIES = 1000;
	private static final int MAX_ENTRIES_PER_DAY = 5;

	private int max;
	private int entries;
	private SimulationLoader loader;
	private TreeMap<Integer, ArrayList<ISimulation>> sims;

	public SimulationCache(SimulationLoader loader) throws IOException {
		this.loader = loader;
		this.sims = new TreeMap<>();
		ISimulation sim = loader.loadSimulation();
		this.max = sim.getConfig().getRounds();
		recycle(sim);
	}

	public synchronized Recyclable<ISimulation> borrow(int day) throws IOException {
		Entry<Integer, ArrayList<ISimulation>> entry = sims.floorEntry(day);
		if (entry == null) {
			return wrap(loader.loadSimulation());
		} else {
			ArrayList<ISimulation> list = entry.getValue();
			try {
				this.entries--;
				return wrap(list.remove(list.size() - 1));
			} finally {
				if (list.isEmpty()) {
					sims.remove(entry.getKey());
				}
			}
		}
	}

	private Recyclable<ISimulation> wrap(ISimulation simulation) {
		return new Recyclable<ISimulation>(simulation) {

			@Override
			protected void recycle(ISimulation item) {
				SimulationCache.this.recycle(item);
			}
		};
	}

	protected synchronized void recycle(ISimulation sim) {
		cleanup();
		ArrayList<ISimulation> list = this.sims.get(sim.getDay());
		if (list == null) {
			list = new ArrayList<>();
			this.sims.put(sim.getDay(), list);
		}
		if (list.size() < MAX_ENTRIES_PER_DAY){
			this.entries++;
			list.add(sim);
		}
	}

	private void cleanup() {
		if (entries > MAX_ENTRIES) {
			this.entries = 0;
			this.sims.clear();
		}
	}

	public synchronized Recyclable<ISimulation> getAny() throws IOException {
		return borrow(max);
	}

}
