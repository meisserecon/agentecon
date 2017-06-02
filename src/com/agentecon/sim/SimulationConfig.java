// Created by Luzius on May 15, 2015

package com.agentecon.sim;

import java.util.ArrayList;
import java.util.Collection;

public class SimulationConfig {
	
	private long seed;
	private int rounds;
	
	private ArrayList<Event> events = new ArrayList<Event>();
	
	@SuppressWarnings("unused")
	private SimulationConfig(){
	}
	
	public SimulationConfig(int rounds){
		this(rounds, 23);
	}
	
	public SimulationConfig(int rounds, int seed){
		this.seed = seed;
		this.rounds = rounds;
	}
	
	public void setSeed(int seed){
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}
	
	public boolean hasAging(){
		return false;
	}
	
	protected Collection<Event> getEvents(){
		return events;
	}

	public void addEvent(Event e) {
		events.add(e);
	}

	public int getRounds() {
		return rounds;
	}
	
}
