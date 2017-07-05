package com.agentecon.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.agentecon.agent.Agent;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.sim.SimulationListeners;

public class Country implements ICountry {

	private int day;
	private Good money;
	private Random rand;
	private Agents agents, backup;
	private long randomBaseSeed;
	private IInnovation innovation;
	private SimulationListeners listeners;

	public Country(SimulationConfig config, SimulationListeners listeners) {
		this.money = config.getMoney();
		this.listeners = listeners;
		this.randomBaseSeed = config.getSeed() + 123123453;
		this.rand = new Random(config.getSeed());
		this.agents = new Agents(listeners, rand.nextLong(), 1);
		this.innovation = config.getInnovation();
	}
	
	public Good getMoney(){
		return money;
	}

	public void handoutEndowments() {
		for (IConsumer c : agents.getConsumers()) {
			c.collectDailyEndowment();
		}
	}

	public void prepareDay(int day) {
		this.day = day;
		// reset random every day to get more consistent results on small changes
		this.rand = new Random(day ^ randomBaseSeed);
		this.agents = this.agents.renew(rand.nextLong());
		this.handoutEndowments();
		this.createFirms();
		this.listeners.notifyDayStarted(day);
	}

	private void createFirms() {
		for (IShareholder shareholder : agents.getShareholders()) {
			if (shareholder instanceof IFounder){
				IFounder founder = (IFounder) shareholder;
				IFirm firm = founder.considerCreatingFirm(innovation);
				if (firm != null){
					add(firm);
				}
			}
		}		
	}

	@Override
	public Random getRand() {
		return rand;
	}

	@Override
	public int getDay() {
		return day;
	}

	public void finishDay(int day) {
		IStock inheritedMoney = new Stock(money);
		Portfolio inheritance = new Portfolio(inheritedMoney);
		Collection<IConsumer> consumers = agents.getConsumers();
		Iterator<IConsumer> iter = consumers.iterator();
		double util = 0.0;
		while (iter.hasNext()) {
			IConsumer c = iter.next();
			assert c.isAlive();
			util += c.consume();
			c.age(inheritance);
		}
		for (Position pos : inheritance.getPositions()) {
			agents.getCompany(pos.getTicker()).inherit(pos);
		}
		if (inheritedMoney.getAmount() > 0) {
			agents.getRandomConsumer().getMoney().absorb(inheritedMoney);
		}

		listeners.notifyDayEnded(day, util / consumers.size());
	}

	public void startTransaction() {
		this.backup = agents.duplicate();
	}

	public void commitTransaction() {
		this.backup = null;
	}

	public void abortTransaction() {
		assert backup != null;
		this.agents = backup;
		this.agents.refreshReferences();
	}

	public Agents getAgents() {
		return agents;
	}

	public void add(IAgent agent) {
		agents.add((Agent) agent);
	}

	public String toString() {
		return "World at day " + day + " with " + agents.getAgents().size() + " agents";
	}

}
