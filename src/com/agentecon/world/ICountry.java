package com.agentecon.world;

import java.util.Random;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;


public interface ICountry {
	
	public Random getRand();

	public int getDay();

	public Agents getAgents();
	
	public void add(IAgent agent);

	public Good getMoney();
	
}
