package com.agentecon.verification;

import java.util.ArrayList;

import com.agentecon.api.SimulationConfig;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.sim.config.IConfiguration;
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
			protected IFirmDecisions getDividendStrategy(double laborShare) {
				return SimpleExplorationScenario.this.createStrategy(laborShare);
			}

		};
		return scenario.createConfiguration(new PriceConfig(false, false), DAYS, RETURNS_TO_SCALE);
	}

	@Override
	public boolean shouldTryAgain() {
		return Numbers.isSmaller(fr, MAX);
	}

	protected IFirmDecisions createStrategy(double laborShare) {
		return new StrategyExploration(laborShare, fr, mode);
	}

	@Override
	public String getComment() {
		return createStrategy(RETURNS_TO_SCALE).toString();
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
				UtilityStats stats = new UtilityStats(500);
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