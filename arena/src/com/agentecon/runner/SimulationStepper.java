package com.agentecon.runner;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.util.LogClock;

public class SimulationStepper {

	private AtomicReference<ISimulation> simulation;
	private AtomicReference<SimulationLoader> loader;

	public SimulationStepper(SimulationHandle handle) throws IOException {
		this.loader = new AtomicReference<SimulationLoader>(new SimulationLoader(handle));
		this.simulation = new AtomicReference<ISimulation>(loader.get().loadSimulation());
//		this.enablePeriodicUpdate();
	}

	public ISimulation getSimulation(int day) throws IOException {
		ISimulation simulation = this.simulation.get();
		assert day <= simulation.getConfig().getRounds();
		if (simulation.getDay() > day) {
			simulation = loader.get().loadSimulation(); // reload, cannot step backwards
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
		}
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

}
