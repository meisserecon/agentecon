package com.agentecon;

import java.util.Date;
import java.util.HashMap;

import com.agentecon.api.IMarket;
import com.agentecon.api.Price;
import com.agentecon.firm.decisions.EExplorationMode;
import com.agentecon.good.Good;
import com.agentecon.metric.IMarketListener;
import com.agentecon.metric.SimulationListenerAdapter;
import com.agentecon.sim.Simulation;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class TimeSeries extends SimulationListenerAdapter implements IMarketListener {

	private Good[] goods;
	private HashMap<Good, Average> prices;

	public TimeSeries(String comment, Good... goods) {
		this.goods = goods;
		this.prices = new InstantiatingHashMap<Good, Average>() {

			@Override
			protected Average create(Good key) {
				return new Average();
			}
		};
		System.out.println(comment + "\t");
		System.out.print("Day");
		for (Good g : goods) {
			System.out.print("\t" + g + " price\t" + g + " volume");
		}
		System.out.println();
	}

	public TimeSeries(String comment, Good[] inputs, Good[] outputs) {
		this(comment, merge(inputs, outputs));
	}

	private static Good[] merge(Good[] inputs, Good[] outputs) {
		Good[] merged = new Good[inputs.length + outputs.length];
		for (int i = 0; i < inputs.length; i++) {
			merged[i] = inputs[i];
		}
		for (int i = 0; i < outputs.length; i++) {
			merged[i + inputs.length] = outputs[i];
		}
		return merged;
	}

	@Override
	public void notifyOffered(Good good, double quantity, Price price) {
	}

	@Override
	public void notifySold(Good good, double quantity, Price price) {
		this.prices.get(good).add(quantity, price.getPrice());
	}

	@Override
	public void notifyTradesCancelled() {
		prices.clear();
	}

	@Override
	public void notifyMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		if (day > 0) {
			System.out.print(day);
			for (Good g : goods) {
				System.out.print("\t" + prices.get(g).getAverage() + "\t" + prices.get(g).getTotWeight());
			}
			System.out.println("\t" + utility);
		}
		notifyTradesCancelled();
	}

	// public static void main(String[] args) {
	// // Optimum is 70.7946 utility with 808.433 labor https://www.wolframalpha.com/input/?i=maximize(10+*+ln+(1+%2B+(h%2F10)%5E0.7))+%2B+14+*+ln+(25+-+h%2F100))
	//// EExplorationMode mode = EExplorationMode.IDEAL_COST; double br = 0.27539 low and double br = 0.2754 high
	// EExplorationMode mode = EExplorationMode.KNOWN; double br = 1.5;
	// SimpleExplorationScenario scenario = new SimpleExplorationScenario(mode, br);
	// Simulation sim = new Simulation(scenario.createNextConfig());
	// sim.addListener(new PriceTimeSeries("Mode " + mode + " with br=" + br + " on " + new Date(), scenario.conf.getInputs(), scenario.conf.getOutputs()));
	// sim.finish();
	// }
	
	private static void printFigure(EExplorationMode mode, double br){
		StabilityProfiles scenario = new StabilityProfiles(mode, br);
		Simulation sim = new Simulation(scenario.createNextConfig());
		sim.addListener(new TimeSeries("Mode " + mode + " with br=" + br + " on " + new Date(), scenario.conf.getInputs(), scenario.conf.getOutputs()));
		sim.finish();
		System.out.println();
	}
	
	// Prints the time series shown in the paper
	public static void main(String[] args) {
		printFigure(EExplorationMode.KNOWN, 0.999);	// figure 1
		printFigure(EExplorationMode.KNOWN, 1.0);	// figure 1
		printFigure(EExplorationMode.KNOWN, 1.001);	// figure 1
		printFigure(EExplorationMode.KNOWN, -1.0);	// figure 3
		printFigure(EExplorationMode.PLANNED, 0.7);	// figure 6
		printFigure(EExplorationMode.PLANNED, 1.41);	// figure 7
		printFigure(EExplorationMode.PLANNED, -0.1105);	// figure 8
	}

}