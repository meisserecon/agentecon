package com.agentecon.verification;

import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.api.IFirm;
import com.agentecon.events.FirmEvent;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.ProfitInterpolation;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.sim.Simulation;
import com.agentecon.sim.config.SimConfig;

public class AdaptiveProfitTest {

	private static final int STEPS = 5000;

	private ProductionStats stats;
	private ProfitStats profits;

	public AdaptiveProfitTest() {
		this.stats = new ProductionStats(STEPS / 2) {
			@Override
			public void notifyFirmCreated(IFirm firm) {
			}
		};
		this.profits = new ProfitStats(STEPS / 2) {
			@Override
			public void notifyFirmCreated(IFirm firm) {
			}
		};
	}

	public Simulation createSimulation(final int seed, final int count) {
		return new Simulation(new StolperSamuelson(1) { // 3.0, new double[] { 0.25, 0.75 }) {

			protected void addFirms(PriceConfig pricing, SimConfig config, double returnsToScale) {
				for (int i = 0; i < outputs.length; i++) {
					final int firmNumber = i;
					Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
					IProductionFunction prodfun = prodWeights.createProdFun(i, returnsToScale);
					config.addEvent(new FirmEvent(FIRMS_PER_TYPE - count, "firm_" + i, end, prodfun, pricing) {

						@Override
						protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf, Random random) {
							return createFirm(type, end, prodFun, pf, getDividendStrategy(returnsToScale));
						}

					});
					config.addEvent(new FirmEvent(count, "firm_" + i, end, prodfun, pricing) {

						@Override
						protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf, Random random) {
							// Producer f = createFirm(type, end, prodFun, pf, getDividendStrategy(returnsToScale));
							Producer f = createFirm(type, end, prodFun, pf, new ProfitInterpolation(0.95, EExplorationMode.KNOWN, random.nextLong()));
							if (firmNumber == 0) {
								f.addFirmMonitor(profits);
								f.addFirmMonitor(stats);
							}
							return f;
						}

					});
				}
			}

			@Override
			protected IFirmDecisions getDividendStrategy(double returnsToScale) {
				return new StrategyExploration(returnsToScale);
			}

			@Override
			protected int getRandomSeed() {
				return seed;
			}
		}.createConfiguration(new PriceConfig(true, true), STEPS, ExplorationScenario.RETURNS_TO_SCALE));
	}

	public void run(int seed, int count) {
		Simulation sim = createSimulation(seed, count);
		sim.addListener(stats);
		sim.addListener(profits);
		sim.run();
		System.out.print(stats.getInputVolume() + "\t" + profits.getInput1Volume() + "\t");
	}

	public static void main(String[] args) {
		// 80.7325211814418		31.59939850258466	
		for (int i = 10; i <= StolperSamuelson.FIRMS_PER_TYPE; i++) {
			new AdaptiveProfitTest().run(3124, i);
			System.out.println();
		}
	}

}