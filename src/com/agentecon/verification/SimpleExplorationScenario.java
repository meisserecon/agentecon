package com.agentecon.verification;

import java.util.ArrayList;

import com.agentecon.agent.Endowment;
import com.agentecon.api.SimulationConfig;
import com.agentecon.events.FirmEvent;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.sim.Simulation;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;

public class SimpleExplorationScenario implements IConfiguration {

	public static final EExplorationMode[] DISCUSSED = new EExplorationMode[] { EExplorationMode.IDEAL_COST, EExplorationMode.EXPECTED, EExplorationMode.KNOWN };

	public static final int DAYS = 5000;

	private static final double MIN = -5.0;
	private static final double MAX = 5.0;
	private static final double INCREMENT = 0.01;

	private static final double RETURNS_TO_SCALE = 0.7;

	private double fr;
	private EExplorationMode mode;

	public SimpleExplorationScenario(EExplorationMode mode) {
		this(mode, MIN);
	}

	public SimpleExplorationScenario(EExplorationMode mode, double fr) {
		this.mode = mode;
		this.fr = fr - INCREMENT;
	}

	@Override
	public SimulationConfig createNextConfig() {
		fr += INCREMENT;
		StolperSamuelson scenario = new StolperSamuelson(1) {

			@Override
			protected int getRandomSeed() {
				return (int) (fr * 10000);
			}

			@Override
			protected void addFirms(PriceConfig pricing, SimConfig config, double returnsToScale) {
				for (int i = 0; i < outputs.length; i++) {
					Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
					for (int f = 0; f < FIRMS_PER_TYPE; f++) {
						final int number = f;
						IProductionFunction prodfun = prodWeights.createProdFun(i, returnsToScale);
						config.addEvent(new FirmEvent(1, "firm_" + i, end, prodfun, pricing) {
							protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf) {
								final IFirmDecisions strategy = createStrategy(number);
								return createFirm(type, end, prodFun, pf, strategy);
							}

							private IFirmDecisions createStrategy(int type) {
								return SimpleExplorationScenario.this.createStrategy();
							}
						});
					}
				}
			}

		};
		return scenario.createConfiguration(new PriceConfig(false, false), DAYS, RETURNS_TO_SCALE);
	}

	@Override
	public boolean shouldTryAgain() {
		return Numbers.isSmaller(fr, MAX);
	}

	protected IFirmDecisions createStrategy() {
		return new StrategyExploration(RETURNS_TO_SCALE, fr, mode);
	}

	@Override
	public String getComment() {
		return createStrategy().toString();
	}

	public static void main(String[] args) {
		ArrayList<Simulation> sims = new ArrayList<Simulation>();
		SimpleExplorationScenario ref = new SimpleExplorationScenario(EExplorationMode.EXPECTED);
		System.out.print("b_R\t");
		for (EExplorationMode mode : DISCUSSED) {
			System.out.print(mode + "\t");
			sims.add(new Simulation(new SimpleExplorationScenario(mode)));
		}
		System.out.println();
		while (!sims.isEmpty()) {
			ArrayList<Simulation> next = new ArrayList<Simulation>();
			ref.createNextConfig();
			System.out.print(ref.fr + "\t");
			for (Simulation sim : sims) {
				UtilityStats stats = new UtilityStats(DAYS/2);
				sim.addListener(stats);
				sim.run();
				System.out.print(stats.getUtility() + "\t");
				sim = (Simulation) sim.getNext();
				if (sim != null) {
					next.add(sim);
				}
			}
			System.out.println();
			sims = next;
		}
	}

}