// Created by Luzius on Apr 22, 2014

package com.agentecon;

import java.util.Collection;
import java.util.Queue;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.StockMarket;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.Producer;
import com.agentecon.firm.Ticker;
import com.agentecon.market.IMarket;
import com.agentecon.sim.ISimulationListener;
import com.agentecon.sim.RepeatedMarket;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.sim.config.TechnologyConfiguration;
import com.agentecon.world.World;

// The world
public class Simulation implements ISimulation, IIteratedSimulation {

	private IConfiguration metaConfig;

	private SimConfig config;

	private int day;
	private Queue<SimEvent> events;
	private SimulationListeners listeners;
	private World world;
	private StockMarket stocks;

	public Simulation() {
		this(new TechnologyConfiguration(1313));
	}
	
	public Simulation(IConfiguration metaConfig) {
		this(metaConfig.createNextConfig());
		this.metaConfig = metaConfig;
	}

	public Simulation(SimulationConfig config) {
		this.config = (SimConfig) config;
		this.events = this.config.createEventQueue();
		this.listeners = new SimulationListeners();
		this.world = new World(config.getSeed(), listeners);
		this.stocks = new StockMarket(world);
		this.day = 0;
	}
	
	@Override
	public boolean hasNext() {
		return metaConfig != null && metaConfig.shouldTryAgain();
	}

	public ISimulation getNext() {
		if (hasNext()) {
			return new Simulation(metaConfig);
		} else {
			return null;
		}
	}

	public String getComment() {
		return metaConfig == null ? null : metaConfig.getComment();
	}

	public void run() {
		forwardTo(config.getRounds());
	}

	@Override
	public void forwardTo(int targetDay) {
		for (; day < targetDay; day++) {
			processEvents(day); //  must happen before daily endowments
			world.prepareDay(day);
			stocks.trade(day);
			RepeatedMarket market = new RepeatedMarket(world, listeners);
			market.iterate(day, config.getIntradayIterations());
			for (Producer firm : world.getFirms().getAllFirms()) {
				firm.produce();
			}
			world.finishDay(day);
		}
	}

	public int getDay() {
		return day;
	}

	@Override
	public boolean isFinished() {
		return day >= config.getRounds();
	}
	
	private void processEvents(int day) {
		while (!events.isEmpty() && events.peek().getDay() <= day) {
			SimEvent event = events.poll();
			event.execute(world);
			listeners.notifyEvent(event);
			if (event.reschedule()) {
				events.add(event);
			}
		}
	}

	@Override
	public SimulationConfig getConfig() {
		return config;
	}

	@Override
	public Collection<? extends IConsumer> getConsumers() {
		return world.getConsumers().getAllConsumers();
	}

	@Override
	public Collection<? extends IFirm> getFirms() {
		return world.getFirms().getAllFirms();
	}
	
	@Override
	public Collection<? extends IAgent> getAgents() {
		return world.getAgents().getAll();
	}
	
	@Override
	public Collection<? extends IFirm> getListedCompanies() {
		return world.getAgents().getPublicCompanies();
	}

	@Override
	public void addListener(ISimulationListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}
	
	@Override
	public void removeListener(ISimulationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public String getName() {
		return "Name";
	}

	@Override
	public String getDescription() {
		return "Description";
	}

	@Override
	public IMarket getStockMarket() {
		return stocks;
	}

	@Override
	public Collection<? extends IShareholder> getShareHolders() {
		return world.getAgents().getShareholders();
	}

	@Override
	public IFirm getListedCompany(Ticker ticker) {
		return world.getAgents().getCompany(ticker);
	}
	
	public static void main(String[] args) {
		Simulation sim = new Simulation();
		sim.run();
	}

}
