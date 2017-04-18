package com.agentecon.verification;

import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.Producer;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.price.PriceFactory;
import com.agentecon.production.IProductionFunction;
import com.agentecon.sim.config.ConsumptionWeights;
import com.agentecon.sim.config.ProductionWeights;
import com.agentecon.sim.config.SimConfig;

public abstract class StolperSamuelson {
	
	public static final Good PIZZA = new Good("Pizza");
	public static final Good FONDUE = new Good("Fondue");
	public static final Good IT_HOUR = new Good("Italian man-hours");
	public static final Good CH_HOUR = new Good("Swiss man-hours");

	public static final int HOURS_PER_DAY = 24;
	public static final int CONSUMERS_PER_TYPE = 100;
	public static final int FIRMS_PER_TYPE = 10;

	protected static final double LOW = 3.0;
	protected static final double HIGH = HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - LOW;

	protected Good[] inputs, outputs;
	protected ProductionWeights prodWeights;
	protected ConsumptionWeights consWeights;

	public StolperSamuelson() {
		this(LOW);
	}
	
	public StolperSamuelson(double fonduePreference) {
		this(fonduePreference, ProductionWeights.DEFAULT_WEIGHTS);
	}

	public StolperSamuelson(double fonduePref, double[] prodWeights) {
		this.inputs = new Good[] { IT_HOUR, CH_HOUR };
		this.outputs = new Good[] { PIZZA, FONDUE };
		this.prodWeights = new ProductionWeights(inputs, prodWeights, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - fonduePref, fonduePref);
	}

	public StolperSamuelson(int size) {
		assert size <= 1; // TODO
		this.inputs = createGoods("input", size);
		this.outputs = createGoods("output", size);
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT);
	}

	private Good[] createGoods(String string, int size) {
		Good[] goods = new Good[size];
		for (int i = 0; i < size; i++) {
			goods[i] = new Good(string + "_" + (i + 1));
		}
		return goods;
	}

	public SimConfig createConfiguration(PriceConfig pricing, int rounds, double returnsToScale) {
		SimConfig config = new SimConfig(rounds, getRandomSeed());
		addFirms(pricing, config, returnsToScale);
		addConsumers(config);
		return config;
	}
	
	protected int getRandomSeed(){
		return 21;
	}

	protected void addSpecialEvents(SimConfig config) {
		updatePrefs(config, 1000, LOW);
	}

	protected void updatePrefs(SimConfig config, int when, final double pizza) {
		config.addEvent(new UpdatePreferencesEvent(when) {

			@Override
			protected void update(com.agentecon.consumer.Consumer c) {
				LogUtil util = (LogUtil) c.getUtilityFunction();
				util = consWeights.createDeviation(util, outputs[0], pizza);
				util = consWeights.createDeviation(util, outputs[1], HIGH + LOW - pizza);
				c.setUtilityFunction(util);
			}

		});
	}

	protected void addConsumers(SimConfig config) {
		for (int i = 0; i < inputs.length; i++) {
			Endowment end = new Endowment(new Stock(inputs[i], HOURS_PER_DAY));
			config.addEvent(new ConsumerEvent(CONSUMERS_PER_TYPE, "cons_" + i, end, consWeights.createUtilFun(i, 0)));
		}
	}

	protected void addFirms(PriceConfig pricing, SimConfig config, double returnsToScale) {
		for (int i = 0; i < outputs.length; i++) {
			Endowment end = new Endowment(new IStock[] { new Stock(SimConfig.MONEY, 1000) }, new IStock[] {});
			IProductionFunction prodfun = prodWeights.createProdFun(i, returnsToScale);
			config.addEvent(new FirmEvent(FIRMS_PER_TYPE, "firm_" + i, end, prodfun, pricing){

				@Override
				protected Producer createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf, Random rand) {
					IFirmDecisions strategy = getDividendStrategy(returnsToScale);
					Producer prod = createFirm(type, end, prodFun, pf, strategy);
					notifyFirmCreated(prod, strategy);
					return prod;
				}
				
			});
		}
	}

	protected void notifyFirmCreated(Producer prod, IFirmDecisions strategy) {
	}

	protected abstract IFirmDecisions getDividendStrategy(double returnsToScale);

	public Good[] getOutputs() {
		return outputs;
	}

	public Good[] getInputs() {
		return inputs;
	}

}
