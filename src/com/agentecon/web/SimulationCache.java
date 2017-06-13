package com.agentecon.web;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

import com.agentecon.classloader.SimulationHandle;
import com.agentecon.runner.SimulationStepper;

public class SimulationCache {

	private HashMap<SimulationHandle, SoftReference<SimulationStepper>> simulations;

	public SimulationCache() {
		this.simulations = new HashMap<>();
	}

	public synchronized SimulationStepper getSimulation(SimulationHandle handle) throws IOException {
		SimulationStepper stepper = get(handle);
		if (stepper == null){
			stepper = new SimulationStepper(handle);
			simulations.put(handle, new SoftReference<SimulationStepper>(stepper));
		}
		return stepper;
	}

	private SimulationStepper get(SimulationHandle handle) {
		SoftReference<SimulationStepper> ref = simulations.get(handle);
		if (ref != null) {
			SimulationStepper stepper = ref.get();
			if (stepper == null) {
				cleanup();
			}
			return stepper;
		} else {
			return null;
		}
	}

	private void cleanup() {
		Iterator<SoftReference<SimulationStepper>> iter = simulations.values().iterator();
		while (iter.hasNext()){
			if (iter.next().get() == null){
				iter.remove();
			}
		}
	}

}
