// Created by Luzius on May 15, 2015

package com.agentecon.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class SimulationConfig {

	private static final int DEFAULT_MARKET_RETRIES = 0;

	private long seed;
	private int rounds;
	private int marketRetries;

	private ArrayList<Event> events = new ArrayList<Event>();

	@SuppressWarnings("unused")
	private SimulationConfig() {
	}

	public SimulationConfig(int rounds) {
		this(rounds, 23);
	}

	public SimulationConfig(int rounds, int seed) {
		this(rounds, seed, DEFAULT_MARKET_RETRIES);
	}

	public SimulationConfig(int rounds, int seed, int wobbles) {
		this.seed = seed;
		this.rounds = rounds;
		this.marketRetries = wobbles;
	}

	public long getSeed() {
		return seed;
	}

	public boolean hasAging() {
		return false;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void addEvent(Event e) {
		events.add(e);
	}

	public int getRounds() {
		return rounds;
	}

	public int getIntradayIterations() {
		return marketRetries;
	}

}
