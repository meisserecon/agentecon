package com.agentecon.runner;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.agentecon.ISimulation;

public class SimulationCache {

	private static final int MIN_CACHE_SIZE = 10;

	private int max;
	private SimulationLoader loader;
	private TreeMap<Integer, ArrayList<SoftReference<ISimulation>>> sims;

	// Keep direct references to a limited number of objects to ensure they
	// cannot be garbage collected
	private int current;
	private ISimulation[] hardReferences;

	public SimulationCache(SimulationLoader loader) throws IOException {
		this.loader = loader;
		this.sims = new TreeMap<>();
		ISimulation sim = loader.loadSimulation();
		this.max = sim.getConfig().getRounds();
		this.current = 0;
		this.hardReferences = new ISimulation[MIN_CACHE_SIZE];
		recycle(sim);
	}

	public synchronized Recyclable<ISimulation> borrow(int day) throws IOException {
		Entry<Integer, ArrayList<SoftReference<ISimulation>>> entry = sims.floorEntry(day);
		if (entry == null) {
			return wrap(loader.loadSimulation());
		} else {
			ArrayList<SoftReference<ISimulation>> list = entry.getValue();
			ISimulation sim = extract(list);
			if (list.isEmpty()) {
				sims.remove(entry.getKey());
			}
			if (sim == null) {
				return borrow(day);
			} else {
				return wrap(sim);
			}
		}
	}

	private ISimulation extract(ArrayList<SoftReference<ISimulation>> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			SoftReference<ISimulation> ref = list.remove(i);
			ISimulation sim = ref.get();
			if (sim != null) {
				return sim;
			}
		}
		return null;
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
		ArrayList<SoftReference<ISimulation>> list = this.sims.get(sim.getDay());
		if (list == null) {
			list = new ArrayList<>();
			this.sims.put(sim.getDay(), list);
		}
		insert(sim, list);
	}

	private void insert(ISimulation sim, ArrayList<SoftReference<ISimulation>> list) {
		hardReferences[current] = sim;
		current = (current + 1) % hardReferences.length;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).get() == null) {
				list.set(i, new SoftReference<ISimulation>(sim));
				return;
			}
		}
		list.add(new SoftReference<ISimulation>(sim));
	}

	public synchronized Recyclable<ISimulation> getAny() throws IOException {
		return borrow(max);
	}

}
