package com.agentecon;

import java.util.Arrays;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.finance.IPublicCompany;
import com.agentecon.firm.Producer;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.decisions.StrategyExploration;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.metric.IFirmListener;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.Simulation;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.util.Average;
import com.agentecon.verification.StolperSamuelson;

public class HeuristicsCompetition {

	public static final int DAYS = 5000;

	private boolean deviate;
	private static final double RETURNS_TO_SCALE = 0.7;

	Good input, output;
	SimConfig conf;
	String[] names = new String[StolperSamuelson.FIRMS_PER_TYPE];
	double[] dividends = new double[StolperSamuelson.FIRMS_PER_TYPE];
	double dividend, production;

	public HeuristicsCompetition(boolean deviate) {
		this.deviate = deviate;
		StolperSamuelson ss = new StolperSamuelson(1) {

			int count = 0;

			@Override
			protected IFirmDecisions getDividendStrategy(double laborShare) {
				return HeuristicsCompetition.this.createStrategy(laborShare, count);
			}

			@Override
			protected void notifyFirmCreated(Producer prod, IFirmDecisions strategy) {
				final int number = count++;
				names[number] = strategy.toString();
				prod.addFirmMonitor(new IFirmListener() {

					@Override
					public void reportResults(IPublicCompany inst, double revenue, double cogs, double expectedProfits) {
					}

					@Override
					public void reportDividend(IPublicCompany inst, double amount) {
						dividends[number] = amount;
						dividend += amount;
					}

					@Override
					public void notifyProduced(IPublicCompany inst, String producer, IStock[] inputs, IStock output) {
						// dividend[number] = inputs[0].getAmount();
						production += output.getAmount();
					}
				});
			}

		};
		this.input = ss.getInputs()[0];
		this.output = ss.getOutputs()[0];
		this.conf = ss.createConfiguration(new PriceConfig(true, true), DAYS, RETURNS_TO_SCALE);
	}

	protected IFirmDecisions createStrategy(double laborShare, int agent) {
		switch (agent) {
		case 9:
			if (deviate) {
				return new StrategyExploration(laborShare, 0.0, EExplorationMode.PLANNED);
			}
		default:
			return new StrategyExploration(laborShare, 1.0 - laborShare, EExplorationMode.PLANNED);
		}
	}

	public void simulate() {
		Simulation sim = new Simulation(conf);
		sim.addListener(new SimulationListenerAdapter() {

			private Average inputPrice, outputPrice;

			@Override
			public void notifyDayStarted(int day) {
				HeuristicsCompetition.this.dividend = 0;
				HeuristicsCompetition.this.production = 0;
				this.inputPrice = new Average();
				this.outputPrice = new Average();
				if (day == 0) {
					String caption = Arrays.toString(names).replace(", ", "\t");
					System.out.println("Day\tUtility\tInput Price\tOutput Price\t" + caption.substring(1, caption.length() - 1));
					// System.out.println("Day\tUtility\tInput Price\tInput Quantity\tOutput Price\tOutput Quantity\tProduction\tDividends\tReal Dividends");
				}
			}

			@Override
			public void notifyMarketOpened(IMarket market) {
				market.addMarketListener(new IMarketListener() {

					@Override
					public void notifyTradesCancelled() {
					}

					@Override
					public void notifySold(Good good, double quantity, Price price) {
						if (good.equals(input)) {
							inputPrice.add(quantity, price.getPrice());
						} else {
							assert good.equals(output);
							outputPrice.add(quantity, price.getPrice());
						}
					}

					@Override
					public void notifyOffered(Good good, double quantity, Price price) {
					}
				});
			}

			@Override
			public void notifyDayEnded(int day, double utility) {
				String prod = Arrays.toString(dividends).replace(", ", "\t");
				System.out.println(day + "\t" + utility + "\t" + inputPrice.getAverage() + "\t" + outputPrice.getAverage() + "\t" + prod.substring(1, prod.length() - 1));
				// System.out.println(day + "\t" + utility + "\t" + inputPrice.getAverage() + "\t" + inputPrice.getTotWeight() + "\t" + outputPrice.getAverage() + "\t" + outputPrice.getTotWeight() +
				// "\t" + production + "\t" + dividend + "\t" + dividend/outputPrice.getAverage());
			}
		});
		sim.finish();
	}

	public static void main(String[] args) {
		System.out.println("Firm dividends with planned heuristics and b_R=0.3");
		new HeuristicsCompetition(false).simulate();
		System.out.println();
		System.out.println("Firm dividends with planned heuristics and b_R=0.3 for all firm except one that deviates to b_R=0.1");
		new HeuristicsCompetition(true).simulate();
	}

}