package com.agentecon.verification;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.LogUtil;
import com.agentecon.events.ConsumerEvent;
import com.agentecon.events.FirmEvent;
import com.agentecon.events.UpdatePreferencesEvent;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Stock;
import com.agentecon.price.PriceConfig;
import com.agentecon.sim.config.ConsumptionWeights;
import com.agentecon.sim.config.ProductionWeights;
import com.agentecon.sim.config.SimConfig;

public class StolperSamuelson {

	protected static final int HOURS_PER_DAY = 24;
	protected static final int CONSUMERS_PER_TYPE = 100;
	protected static final int FIRMS_PER_TYPE = 10;

	protected static final double LOW = 3.0;
	protected static final double HIGH = HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - LOW;

	protected Good[] inputs, outputs;
	protected ProductionWeights prodWeights;
	protected ConsumptionWeights consWeights;

	public StolperSamuelson() {
		this(LOW);
	}

	public StolperSamuelson(double low) {
		this.inputs = new Good[] { new Good("Italian man-hours"), new Good("Swiss man-hours") };
		this.outputs = new Good[] { new Good("Pizza"), new Good("Fondue") };
		this.prodWeights = new ProductionWeights(inputs, outputs);
		this.consWeights = new ConsumptionWeights(inputs, outputs, HOURS_PER_DAY - ConsumptionWeights.TIME_WEIGHT - low, low);
		// PriceFactory.NORMALIZED_GOOD = outputs[0];
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
		return 25;
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
			config.addEvent(new FirmEvent(FIRMS_PER_TYPE, "firm_" + i, end, prodfun, pricing));
		}
	}

	public Good[] getOutputs() {
		return outputs;
	}

	public Good[] getInputs() {
		return outputs;
	}

}
