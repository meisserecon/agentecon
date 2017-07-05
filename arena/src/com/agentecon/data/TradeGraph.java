package com.agentecon.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationListenerAdapter;

public class TradeGraph extends SimulationListenerAdapter implements ISimulationListener, IMarketListener {
	
	private int days;
	private ISimulation simulation;
	private ArrayList<Node> nodes;
	private HashMap<Edge, Edge> edges;

	public TradeGraph(ISimulation simulation, List<String> agents) {
		this.nodes = new ArrayList<>();
		this.edges = new HashMap<>();
		this.simulation = simulation;
		this.simulation.addListener(this);
		this.days = 0;
		for (String agent: agents){
			nodes.add(new Node(agent));
		}
		Collections.sort(nodes); // most specific nodes must be first
	}

	public TradeGraphData fetchData() {
		assert days >= 1;
		for (Node n: nodes){
			n.fetchData(simulation.getAgents());
		}
		for (Edge edge: edges.values()){
			edge.finish(days);
		}
		this.simulation.removeListener(this);
		return new TradeGraphData(nodes, edges.values());
	}
	
	class TradeGraphData extends JsonData {
		
		private Collection<Node> nodes;
		private ArrayList<Edge> edges;

		public TradeGraphData(ArrayList<Node> nodes, Collection<Edge> values) {
			this.nodes = nodes;
			this.edges = new ArrayList<>(values);
			Collections.sort(edges);
		}
		
	}
	
	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		days++;
		market.addMarketListener(this);
	}

	@Override
	public void notifyTradesCancelled() {
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
		for (int i=nodes.size() - 1; i>=0; i--){
			Node node1 = nodes.get(i);
			if (node1.contains(seller)){
				for (int j=nodes.size() - 1; j>=0; j--){
					Node node2 = nodes.get(j);
					if (node2.contains(buyer)){
						Edge edge = new Edge(node1, node2, good);
						Edge existing = edges.get(edge);
						if (existing == null){
							edges.put(edge, edge);
							existing = edge;
						}
						existing.include(quantity, payment);
						return; // ensure that every trade is only recorded once
					}
				}
			}
		}
	}

	@Override
	public void notifyMarketClosed(int day) {
	}
	
}
