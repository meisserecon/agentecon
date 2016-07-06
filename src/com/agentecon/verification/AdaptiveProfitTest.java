package com.agentecon.verification;

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

	private ProductionStats stats;
	private ProfitStats profits;

	public AdaptiveProfitTest() {
		this.stats = new ProductionStats(StolperSamuelson.IT_HOUR, 1000){
			@Override
			public void notifyFirmCreated(IFirm firm) {
			}
		};
		this.profits = new ProfitStats(1000){
			@Override
			public void notifyFirmCreated(IFirm firm) {
			}
		};
	}

	public Simulation createSimulation(){
		return new Simulation(new StolperSamuelson(3.0, new double[] { 0.25, 0.75 }) {

			private final int COUNT = 1;

			protected void addFirms(PriceConfig pricing, SimConfig config, double returnsToScale) {
				for (int i = 0; i < outputs.length; i++) {
					int count = i == 0 ? COUNT : 0;
					Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
					IProductionFunction prodfun = prodWeights.createProdFun(i, returnsToScale);
					config.addEvent(new FirmEvent(FIRMS_PER_TYPE - count, "firm_" + i, end, prodfun, pricing) {

						@Override
						protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf) {
							return createFirm(type, end, prodFun, pf, getDividendStrategy(returnsToScale));
						}

					});
					config.addEvent(new FirmEvent(count, "firm_" + i, end, prodfun, pricing) {

						@Override
						protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf) {
//							Producer f = createFirm(type, end, prodFun, pf, getDividendStrategy(returnsToScale));
							Producer f = createFirm(type, end, prodFun, pf, new ProfitInterpolation(200, 5, EExplorationMode.KNOWN));
							f.addFirmMonitor(profits);
							f.addFirmMonitor(stats);
							return f;
						}

					});
				}
			}

			@Override
			protected IFirmDecisions getDividendStrategy(double returnsToScale) {
				return new StrategyExploration(returnsToScale);
			}
		}.createConfiguration(new PriceConfig(true, true), 2000, 0.7));
	}
	
	public void run(){
		Simulation sim = createSimulation();
		sim.addListener(stats);
		sim.addListener(profits);
		sim.run();
		System.out.print(stats.getInputVolume() + "\t" + profits.getInput1Volume() + "\t");
	}
	
	public static void main(String[] args) {
		new AdaptiveProfitTest().run(); // 32.23650926025163	113.0639263879158
	}

}