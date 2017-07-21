package com.agentecon.web.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.agentecon.ISimulation;

public class SelectionRecommendation {
	
	private HashSet<String> consumers, firms;
	
	public SelectionRecommendation(ISimulation simulation){
		this.firms = new HashSet<>(simulation.getAgents().getFirmTypes());
		this.consumers = new HashSet<>(simulation.getAgents().getConsumerTypes());
	}

	public Collection<String> getNewNodeSuggestions(ISimulation simulation){
		ArrayList<String> newTypes = new ArrayList<>();
		for (String type: simulation.getAgents().getConsumerTypes()){
			if (!consumers.contains(type)){
				newTypes.add(type);
			}
		}
		for (String type: simulation.getAgents().getFirmTypes()){
			if (!firms.contains(type)){
				newTypes.add(type);
			}
		}
		return newTypes;
	}

}
