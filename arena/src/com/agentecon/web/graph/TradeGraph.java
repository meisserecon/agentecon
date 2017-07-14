package com.agentecon.web.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.query.ENodeType;

public class TradeGraph extends SimulationListenerAdapter implements ISimulationListener, IMarketListener {

	private int days;
	private ISimulation simulation;
	private HashMap<Edge, Edge> edges;
	private ArrayList<Node> firms, consumers;

	public TradeGraph(ISimulation simulation, List<String> agents) {
		this.firms = new ArrayList<>();
		this.consumers = new ArrayList<>();
		this.edges = new HashMap<>();
		this.simulation = simulation;
		this.simulation.addListener(this);
		this.days = 0;
		for (String agent : agents) {
			Node node = new Node(agent);
			ENodeType type = node.getType(simulation.getAgents());
			switch (type) {
			case CONSUMER:
				node.initializeSizeQuery(ESizeType.DEFAULT_CONSUMER_TYPE, simulation);
				consumers.add(node);
				break;
			case FIRM:
				node.initializeSizeQuery(ESizeType.DEFAULT_FIRM_TYPE, simulation);
				firms.add(node);
				break;
			default:
				break;
			}
		}
		Collections.sort(firms); // most specific nodes must be first
		Collections.sort(consumers); // most specific nodes must be first
	}

	public TradeGraphData fetchData() {
		assert days >= 0;
		for (Node n : firms) {
			n.fetchData(simulation.getStatistics(), simulation.getAgents());
		}
		for (Node n : consumers) {
			n.fetchData(simulation.getStatistics(), simulation.getAgents());
		}
		for (Edge edge : edges.values()) {
			edge.finish(days);
		}
		this.simulation.removeListener(this);
		return new TradeGraphData(firms, consumers, edges.values());
	}

	class TradeGraphData extends JsonData {

		public Collection<Node> firms, consumers;
		public ArrayList<Edge> edges;

		public TradeGraphData(ArrayList<Node> firms, ArrayList<Node> consumers, Collection<Edge> values) {
			this.firms = firms;
			this.consumers = consumers;
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
		Node node1 = findMostSpecific(seller);
		Node node2 = findMostSpecific(buyer);

		Edge edge = new Edge(node1, node2, good);
		Edge existing = edges.get(edge);
		if (existing == null) {
			edges.put(edge, edge);
			existing = edge;
		}
		existing.include(quantity, payment);
		return; // ensure that every trade is only recorded once
	}

	private Node findMostSpecific(IAgent agent) {
		if (agent instanceof IConsumer) {
			return findMostSpecific(consumers, agent);
		} else {
			assert agent instanceof IFirm;
			return findMostSpecific(firms, agent);
		}
	}

	private Node findMostSpecific(ArrayList<Node> candidates, IAgent agent) {
		for (int i = candidates.size() - 1; i >= 0; i--) {
			Node n = candidates.get(i);
			if (n.contains(agent)) {
				return n;
			}
		}
		throw new RuntimeException("Could not find a node that matches " + agent);
	}

	@Override
	public void notifyMarketClosed(int day) {
	}

}
