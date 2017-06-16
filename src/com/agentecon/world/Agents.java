package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.agentecon.agent.Agent;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Producer;
import com.agentecon.firm.Ticker;
import com.agentecon.production.IProducer;
import com.agentecon.sim.ISimulationListener;

public class Agents implements IAgents {

	private long seed;
	private Random rand;

	private HashMap<Integer, Agent> all;
	private ArrayList<IConsumer> consumers;
	private HashMap<Ticker, IFirm> firms;
	private ArrayList<IProducer> producers;
	// private ArrayList<Fundamentalist> fundies;
	private ArrayList<IMarketMaker> marketMakers;
	private ArrayList<IShareholder> shareholders;

	private HashSet<String> consumerTypes;
	private HashSet<String> firmTypes;

	private ISimulationListener listeners;

	public Agents(ISimulationListener listeners, long seed) {
		this.firms = new HashMap<>();
		this.all = new HashMap<>();
		this.consumers = new ArrayList<>();
		this.shareholders = new ArrayList<>();
		this.producers = new ArrayList<>();
		this.marketMakers = new ArrayList<>();
		this.consumerTypes = new HashSet<>();
		this.firmTypes = new HashSet<>();
		this.listeners = listeners;
		this.seed = seed;
	}

	public Collection<IFirm> getFirms() {
		return firms.values();
	}

	public Collection<IProducer> getProducers() {
		return producers;
	}

	public Collection<IConsumer> getConsumers() {
		return consumers;
	}

	public Collection<IMarketMaker> getRandomizedMarketMakers() {
		Collections.shuffle(marketMakers, getRand());
		return marketMakers;
	}

	public IFirm getCompany(Ticker ticker) {
		return firms.get(ticker);
	}

	public void add(Agent agent) {
		include(agent, true);
	}

	private void include(Agent agent, boolean newAgent) {
		all.put(agent.getAgentId(), agent);
		if (agent instanceof IFirm) {
			IFirm firm = (IFirm) agent;
			firms.put(firm.getTicker(), firm);
			firmTypes.add(firm.getType());
			if (newAgent) {
				for (IMarketMaker mm : marketMakers) {
					mm.notifyFirmCreated(firm);
				}
			}
		}
		if (agent instanceof IShareholder) {
			shareholders.add((IShareholder) agent);
		}
		if (agent instanceof IMarketMaker) {
			marketMakers.add((IMarketMaker) agent);
		}
		if (agent instanceof Producer) {
			producers.add((Producer) agent);
		}
		if (agent instanceof Consumer) {
			consumers.add((Consumer) agent);
			consumerTypes.add(agent.getType());
		}
		if (listeners != null && newAgent) {
			listeners.notifyAgentCreated(agent);
		}
	}

	public Collection<IConsumer> getRandomConsumers() {
		return getRandomConsumers(-1);
	}

	public Collection<? extends IAgent> getAgents() {
		return all.values();
	}

	@SuppressWarnings("unchecked")
	public Collection<IConsumer> getRandomConsumers(int cardinality) {
		Collections.shuffle(consumers, getRand()); // OPTIMIZABLE in case of
													// cardinality < size
		if (cardinality == -1 || cardinality >= consumers.size()) {
			return (Collection<IConsumer>) consumers.clone();
		} else {
			return consumers.subList(0, cardinality);
		}
	}

	public Collection<IProducer> getRandomFirms() {
		return getRandomFirms(-1);
	}

	public Collection<IProducer> getRandomFirms(int cardinality) {
		Collections.shuffle(producers, getRand());
		if (cardinality < 0 || cardinality >= producers.size()) {
			return producers;
		} else {
			return producers.subList(0, cardinality);
		}
	}

	public IConsumer getRandomConsumer() {
		return consumers.get(getRand().nextInt(consumers.size()));
	}

	private Random getRand() {
		if (rand == null) {
			rand = new Random(seed);
		}
		return rand;
	}

	public Collection<? extends IShareholder> getShareholders() {
		return shareholders;
	}

	@SuppressWarnings("unchecked")
	public Collection<IShareholder> getRandomShareholders() {
		Collections.shuffle(shareholders, getRand());
		return (Collection<IShareholder>) shareholders.clone();
	}

	public Agents renew(long seed) {
		Agents copy = new Agents(listeners, seed);
		for (Agent a : all.values()) {
			if (a.isAlive()) {
				copy.include(a, false);
			} else {
				listeners.notifyAgentDied(a);
			}
		}
		return copy;
	}

	public Agents duplicate() {
		long seed = getCurrentSeed();
		assert rand == null;
		Agents duplicate = new Agents(listeners, seed);
		for (Agent a : all.values()) {
			duplicate.include(a.clone(), false);
		}
		return duplicate;
	}

	private long getCurrentSeed() {
		if (rand != null) {
			seed = rand.nextLong();
			rand = null;
		}
		return seed;
	}

	@Override
	public String toString() {
		return consumers.size() + " consumers, " + producers.size() + " firms";
	}

	public void refreshReferences() {
		for (Agent a : all.values()) {
			a.refreshRef();
		}
	}

	public Collection<IMarketMaker> getAllMarketMakers() {
		return marketMakers;
	}

	@Override
	public Collection<? extends IShareholder> getShareHolders() {
		return shareholders;
	}

	@Override
	public IFirm getFirm(Ticker ticker) {
		return firms.get(ticker);
	}

	@Override
	public IAgent getAgent(int agentId) {
		return all.get(agentId);
	}

	@Override
	public Collection<String> getFirmTypes() {
		return firmTypes;
	}

	@Override
	public Collection<String> getConsumerTypes() {
		return consumerTypes;
	}

	@Override
	public Collection<IAgent> getAgents(String type) {
		if (consumerTypes.contains(type)){
			return extract(getConsumers(), type);
		} else {
			assert firmTypes.contains(type) : "Agent type " + type + " not found";
			return extract(getFirms(), type);
		}
	}

	private Collection<IAgent> extract(Collection<? extends IAgent> agents, String type) {
		ArrayList<IAgent> matches = new ArrayList<>();
		for (IAgent candidate: agents){
			if (candidate.getType().equals(type)){
				matches.add(candidate);
			}
		}
		return matches;
	}

}
