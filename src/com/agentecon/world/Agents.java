package com.agentecon.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.agentecon.agent.Agent;
import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.Consumer;
import com.agentecon.finance.Fundamentalist;
import com.agentecon.finance.MarketMaker;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Producer;
import com.agentecon.firm.Ticker;
import com.agentecon.sim.ISimulationListener;

public class Agents implements IAgents {

	private long seed;
	private Random rand;

	private HashMap<Integer, Agent> all;
	private ArrayList<Consumer> consumers;
	private HashMap<Ticker, IFirm> firms;
	private ArrayList<Producer> producers;
	private ArrayList<Fundamentalist> fundies;
	private ArrayList<MarketMaker> marketMakers;
	private ArrayList<IShareholder> shareholders;

	private ISimulationListener listeners;

	public Agents(ISimulationListener listeners, long seed) {
		this(listeners, seed, new ArrayList<Agent>());
	}

	public Agents(ISimulationListener listeners, long seed, Collection<Agent> all) {
		this.firms = new HashMap<>();
		this.all = new HashMap<>();
		this.consumers = new ArrayList<>();
		this.shareholders = new ArrayList<>();
		this.producers = new ArrayList<>();
		this.marketMakers = new ArrayList<>();
		this.fundies = new ArrayList<>();
		for (Agent a : all) {
			if (a.isAlive()) {
				add(a);
			} else {
				listeners.notifyAgentDied(a);
			}
		}
		this.listeners = listeners; // must be at the end to avoid unnecessary notifications
		this.seed = seed;
	}

	public Collection<Producer> getFirms() {
		return producers;
	}

	public Collection<Consumer> getConsumers() {
		return consumers;
	}

	public Collection<MarketMaker> getRandomizedMarketMakers() {
		Collections.shuffle(marketMakers, getRand());
		return marketMakers;
	}

	public IFirm getCompany(Ticker ticker) {
		return firms.get(ticker);
	}

	public void add(Agent agent) {
		all.put(agent.getAgentId(), agent);
		if (agent instanceof IFirm) {
			IFirm pc = (IFirm) agent;
			firms.put(pc.getTicker(), pc);
		}
		if (agent instanceof IShareholder) {
			shareholders.add((IShareholder) agent);
		}
		if (agent instanceof Fundamentalist) {
			fundies.add((Fundamentalist) agent);
		}
		if (agent instanceof MarketMaker) {
			marketMakers.add((MarketMaker) agent);
		}
		if (agent instanceof Producer) {
			producers.add((Producer) agent);
		}
		if (agent instanceof Consumer) {
			consumers.add((Consumer) agent);
		}
		if (listeners != null) {
			listeners.notifyAgentCreated(agent);
		}
	}

	public Collection<Consumer> getRandomConsumers() {
		return getRandomConsumers(-1);
	}

	public Collection<? extends IAgent> getAgents() {
		return all.values();
	}

	@SuppressWarnings("unchecked")
	public Collection<Consumer> getRandomConsumers(int cardinality) {
		Collections.shuffle(consumers, getRand()); // OPTIMIZABLE in case of
													// cardinality < size
		if (cardinality == -1 || cardinality >= consumers.size()) {
			return (Collection<Consumer>) consumers.clone();
		} else {
			return consumers.subList(0, cardinality);
		}
	}

	public Collection<Producer> getRandomFirms() {
		return getRandomFirms(-1);
	}

	public Collection<Producer> getRandomFirms(int cardinality) {
		Collections.shuffle(producers, getRand());
		if (cardinality < 0 || cardinality >= producers.size()) {
			return producers;
		} else {
			return producers.subList(0, cardinality);
		}
	}

	public Consumer getRandomConsumer() {
		return consumers.get(getRand().nextInt(consumers.size()));
	}

	private Random getRand() {
		if (rand == null) {
			rand = new Random(seed);
		}
		return rand;
	}

	public Collection<IFirm> getPublicCompanies() {
		return firms.values();
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
		return new Agents(listeners, seed, all.values());
	}

	public Agents duplicate() {
		preserveRand();
		assert rand == null;
		ArrayList<Agent> allDup = new ArrayList<>(all.size());
		for (Agent a : all.values()) {
			allDup.add(a.clone());
		}
		return new Agents(listeners, seed, allDup);
	}

	private void preserveRand() {
		if (rand != null) {
			seed = rand.nextLong();
			rand = null;
		}
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

	public Collection<MarketMaker> getAllMarketMakers() {
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

}
