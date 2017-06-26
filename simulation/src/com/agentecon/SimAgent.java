package com.agentecon;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;

public class SimAgent extends Agent {
	
	/**
	 * Moved numbering down into simulation project to ensure the numbering
	 * is reset each time the simulation is reloaded.
	 */
	private static int NUMBER = 1;

	public SimAgent(Endowment end) {
		super(end, NUMBER++);
	}

}
