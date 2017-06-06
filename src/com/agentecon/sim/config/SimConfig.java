// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.sim.config;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.agentecon.events.SimEvent;
import com.agentecon.sim.Event;
import com.agentecon.sim.SimulationConfig;

public class SimConfig extends SimulationConfig {
	
	private static final int DEFAULT_WOBBLES = 0;
	
	private int wobbles;
	
	public SimConfig(int rounds) {
		super(rounds);
		this.wobbles = DEFAULT_WOBBLES;
	}
	
	public SimConfig(int rounds, int seed) {
		this(rounds, seed, DEFAULT_WOBBLES);
	}
	
	public SimConfig(int rounds, int seed, int wobbles) {
		super(rounds, seed);
		this.wobbles = wobbles;
	}
	
	public Queue<SimEvent> createEventQueue() {
		PriorityBlockingQueue<SimEvent> queue = new PriorityBlockingQueue<>();
		for (Event e: getEvents()){
			queue.add((SimEvent) e);
		}
		return queue;
	}
	
	public int getIntradayIterations() {
		return wobbles;
	}
	
}
