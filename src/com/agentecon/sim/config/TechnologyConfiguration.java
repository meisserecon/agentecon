package com.agentecon.sim.config;

import java.util.ArrayList;

import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.finance.MarketMaker;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.goods.Good;
import com.agentecon.goods.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.ranking.ConsumerRanking;
import com.agentecon.sim.SimulationConfig;
import com.agentecon.verification.PriceMetric;
import com.agentecon.world.IWorld;

public class TechnologyConfiguration implements IConfiguration {

	public static final int CONSUMERS_PER_TYPE = 100;
	public static final int FIRMS_PER_TYPE = 10;
	private static final int MARKET_MAKERS = 5;

	public static final int ROUNDS = 10000;

	protected int firmsPerType;
	protected int consumersPerType;

	protected Good[] inputs, outputs;

	public TechnologyConfiguration(int seed) {
		this(FIRMS_PER_TYPE, CONSUMERS_PER_TYPE, 3, 3, seed);
	}

	public TechnologyConfiguration(int firmsPerType, int consumersPerType, int consumerTypes, int firmTypes, int seed) {
		this.firmsPerType = firmsPerType;
		this.consumersPerType = consumersPerType;
		this.createGoods(consumerTypes, firmTypes);
	}

	protected void createGoods(int consumerTypes, int firmTypes) {
		this.inputs = new Good[consumerTypes];
		for (int i = 0; i < consumerTypes; i++) {
			inputs[i] = new Good("input " + i, 0.0);
		}
		this.outputs = new Good[firmTypes];
		for (int i = 0; i < firmTypes; i++) {
			outputs[i] = new Good("output " + i, 1.0);
		}
	}

	public SimulationConfig createNextConfig() {
		ArrayList<SimEvent> events = new ArrayList<>();
		addFirms(events, new ProductionWeights(inputs, outputs));
		addConsumers(events, new ConsumptionWeights(inputs, outputs));

		SimulationConfig config = new SimConfig(ROUNDS, 1313);
		for (SimEvent event : events) {
			config.addEvent(event);
		}
		config.addEvent(new SimEvent(0, MARKET_MAKERS) {

			@Override
			public void execute(IWorld sim) {
				for (int i = 0; i < getCardinality(); i++) {
					sim.add(new MarketMaker(sim.getAgents().getPublicCompanies()));
				}
			}
		});
		return config;
	}

	public String getComment() {
		return "Basic tech config";
	}

	protected void addConsumers(ArrayList<SimEvent> config, ConsumptionWeights defaultPrefs) {
		for (int i = 0; i < inputs.length; i++) {
			final int typeNumber = i;
			String name = "Consumer " + i;
			Endowment end = new Endowment(new Stock(inputs[i], Endowment.HOURS_PER_DAY));
			config.add(new ConsumerEvent(consumersPerType, name, end, defaultPrefs.getFactory(i)) {
				@Override
				protected Consumer createConsumer() {
					if (typeNumber == 3) {
						return new Consumer(type, ROUNDS, end, utilFun.create(count++));
					} else {
						return new Consumer(type, end, utilFun.create(count++));
					}
				}
			});
		}
	}

	protected void addFirms(ArrayList<SimEvent> config, ProductionWeights prod) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(new Stock[] { new Stock(Good.MONEY, 1000), new Stock(outputs[i], 10) }, new Stock[] {});
			IProductionFunction fun = prod.createProdFun(i, 0.7);
			config.add(new FirmEvent(firmsPerType, "Firm " + i, end, fun, PriceConfig.DEFAULT));
		}
	}

	public boolean shouldTryAgain() {
		return false;
	}

	public static void main(String[] args) {
		Simulation sim = new Simulation(new TechnologyConfiguration(13));
		ConsumerRanking ranking = new ConsumerRanking();
		sim.addListener(ranking);
		PriceMetric metric1 = new PriceMetric(1000);
		// PricePrinter pp = new PricePrinter(ReincarnatingConsumer.START,
		// ROUNDS);
		sim.addListener(metric1);
		// sim.addListener(pp);
		sim.run();
		metric1.printResult(System.out);
		ranking.print(System.out);
	}

}
