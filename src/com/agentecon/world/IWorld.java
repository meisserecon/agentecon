package com.agentecon.world;

import java.util.Random;

import com.agentecon.agent.IAgent;


public interface IWorld {
	
	public Random getRand();

	public int getDay();

	public Agents getAgents();
	
	public void add(IAgent agent);
	
}
