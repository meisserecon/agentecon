// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.sim.config;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.agentecon.api.Event;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.SimEvent;
import com.agentecon.good.Good;

public class SimConfig extends SimulationConfig {
	
	public static final Good MONEY = new Good("Taler");

	public SimConfig(int rounds) {
		super(rounds);
	}
	
	public SimConfig(int rounds, int seed) {
		super(rounds, seed);
	}
	
	public Queue<SimEvent> createEventQueue() {
		PriorityBlockingQueue<SimEvent> queue = new PriorityBlockingQueue<>();
		for (Event e: getEvents()){
			queue.add((SimEvent) e);
		}
		return queue;
	}
	
}
