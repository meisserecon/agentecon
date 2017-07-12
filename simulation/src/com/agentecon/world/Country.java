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
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Stock;
import com.agentecon.market.IStatistics;
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

	public Good getMoney() {
		return money;
	}

	public void handoutEndowments() {
		for (IConsumer c : agents.getConsumers()) {
			c.collectDailyEndowment();
		}
	}

	public void prepareDay(IStatistics stats) {
		this.day = stats.getDay();
		// reset random every day to get more consistent results on small
		// changes
		this.rand = new Random(day ^ randomBaseSeed);
		this.agents = this.agents.renew(rand.nextLong());
		this.handoutEndowments();
		this.createFirms(stats);
		this.listeners.notifyDayStarted(day);
	}

	private void dismantleFirms(IStatistics stats) {
		for (IFirm firm : agents.getFirms()) {
			if (firm.wantsBankruptcy(stats)) {
				Inventory inv = new Inventory(getMoney());
				Portfolio port = new Portfolio(inv.getMoney());
				double totalshares = firm.dispose(inv, port);
				IShareholder last = null;
				for (IShareholder shareholder : agents.getShareholders()) {
					Position pos = shareholder.getPortfolio().getPosition(firm.getTicker());
					if (pos != null) {
						last = shareholder;
						double shares = pos.getAmount();
						double ratio = Math.min(shares / totalshares, 1.0);
						shareholder.getInventory().absorb(ratio, inv);
						shareholder.getPortfolio().absorbPositions(ratio, port);
						totalshares -= shares;
						if (totalshares <= 0.0) {
							break;
						}
					}
				}
				last.getInventory().absorb(inv);
				last.getPortfolio().absorb(port);
				port.dispose();
			}
		}
	}

	private void createFirms(IStatistics stats) {
		for (IShareholder shareholder : agents.getShareholders()) {
			if (shareholder instanceof IFounder) {
				IFounder founder = (IFounder) shareholder;
				IFirm firm = founder.considerCreatingFirm(stats, innovation, this);
				if (firm != null) {
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

	public void finishDay(IStatistics stats) {
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
		
		dismantleFirms(stats);

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

	@Override
	public int createUniqueAgentId() {
		return agents.createUniqueAgentId();
	}

}
