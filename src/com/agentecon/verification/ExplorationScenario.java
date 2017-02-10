package com.agentecon.verification;

import com.agentecon.api.SimulationConfig;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.config.IConfiguration;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;

public class ExplorationScenario implements IConfiguration {

	public static final EExplorationMode[] DISCUSSED = new EExplorationMode[] { EExplorationMode.OPTIMAL_COST, EExplorationMode.PLANNED, EExplorationMode.KNOWN };

	public static final int DAYS = 2000;

	private static final double MIN = -0.0;
	private static final double MAX = 5.0;
	private static final double INCREMENT = 0.05;
	
	public static final double RETURNS_TO_SCALE = 0.7;
	
	protected static final double STEP = 0.1;
	protected static final double STEPSTEP = 0.05;

	private double fr;
	StolperSamuelson conf;
	private EExplorationMode mode;

	public ExplorationScenario(EExplorationMode mode) {
		this(mode, MIN);
	}

	public ExplorationScenario(EExplorationMode mode, double fr) {
		this.mode = mode;
		this.fr = fr - INCREMENT;
	}

	@Override
	public SimulationConfig createNextConfig() {
		fr += INCREMENT;
		this.conf = new StolperSamuelson() {

			@Override
			protected void addSpecialEvents(SimConfig config) {
				double val = HIGH;
				double step = STEP;
				for (int i = 1000; i < DAYS; i += 250) {
					val -= step;
					step += STEPSTEP;
					super.updatePrefs(config, i, val);
				}
			}
			
			@Override
			protected int getRandomSeed() {
				return (int) (fr * 10000);
			}

			@Override
			protected IFirmDecisions getDividendStrategy(double laborShare) {
				return ExplorationScenario.this.createStrategy(laborShare);
			}

		};
		return conf.createConfiguration(new PriceConfig(true, true), DAYS, RETURNS_TO_SCALE);
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

}
