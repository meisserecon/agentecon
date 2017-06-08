// Created by Luzius on Apr 28, 2014

package com.agentecon.firm;

import java.util.Arrays;

import com.agentecon.agent.Endowment;
import com.agentecon.finance.Firm;
import com.agentecon.firm.decisions.ExpectedRevenueBasedStrategy;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.IPriceProvider;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IPriceFactory;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.ProducerListeners;

public class Producer extends Firm implements IProducer {

	protected InputFactor[] inputs;
	protected OutputFactor output;
	private IProductionFunction prod;

	protected IPriceFactory prices;
	private IFirmDecisions strategy;
	
	private ProducerListeners listeners;

	public Producer(String type, Endowment end, IProductionFunction prod, IPriceFactory prices) {
		this(type, end, prod, prices, new ExpectedRevenueBasedStrategy(((CobbDouglasProduction)prod).getTotalWeight()));
	}

	public Producer(String type, Endowment end, IProductionFunction prod, IPriceFactory prices, IFirmDecisions strategy) {
		super(type, end);
		this.prod = prod;
		this.prices = prices;
		this.setStrategy(strategy);

		Good[] inputs = prod.getInput();
		this.inputs = new InputFactor[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			this.inputs[i] = createInputFactor(prices, getStock(inputs[i]));
		}
		IStock outStock = getStock(prod.getOutput());
		this.output = createOutputFactor(prices, outStock);
		this.listeners = new ProducerListeners();
	}
	
	@Override
	public void addProducerMonitor(IProducerListener listener) {
		this.listeners.add(listener);
	}

	public void setStrategy(IFirmDecisions strategy) {
		this.strategy = strategy.duplicate();
	}

	protected OutputFactor createOutputFactor(IPriceFactory prices, IStock outStock) {
		return new OutputFactor(outStock, prices.createPrice(outStock.getGood()));
	}

	protected InputFactor createInputFactor(IPriceFactory prices, IStock stock) {
		return new InputFactor(stock, prices.createPrice(stock.getGood()));
	}

	protected boolean isFractionalSpending() {
		return false;
	}

	public void offer(IPriceMakerMarket market) {
		double budget = getMoney().getAmount();
		double totSalaries = strategy.calcCogs(budget, prod.getCostOfMaximumProfit(new IPriceProvider() {

			@Override
			public double getPrice(Good output) {
				return Producer.this.getFactor(output).getPrice();
			}
		}));
		if (!getMoney().isEmpty()) {
			for (InputFactor f : inputs) {
				if (f.isObtainable()) {
					double amount = prod.getExpenses(f.getGood(), f.getPrice(), totSalaries);
					if (amount > 0) {
						f.createOffers(market, this, getMoney(), amount);
					} else {
						// so we find out about the true price even if we are not interested
						createSymbolicOffer(market, f);
					}
				} else {
					// in case it becomes available
					createSymbolicOffer(market, f);
				}
			}
		}
		output.createOffers(market, this, getMoney(), output.getStock().getAmount());
	}

	private void createSymbolicOffer(IPriceMakerMarket market, InputFactor f) {
		if (getMoney().getAmount() > 100) {
			f.createOffers(market, this, getMoney(), 1);
		}
	}
	
	public void notifyMarketClosed(){
		adaptPrices();
	}

	public void adaptPrices() {
		for (InputFactor input : inputs) {
			input.adaptPrice();
		}
		output.adaptPrice();
	}

	public void produce() {
		IStock[] inputAmounts = new IStock[inputs.length];
		double cogs = 0.0;
		for (int i = 0; i < inputs.length; i++) {
			cogs += inputs[i].getVolume();
			inputAmounts[i] = inputs[i].getStock().duplicate();
		}
		double produced = prod.produce(getInventory());
		listeners.notifyProduced(this, getType(), inputAmounts, new Stock(output.getGood(), produced));
		listeners.reportResults(this, output.getVolume(), cogs, produced * output.getPrice() - cogs);
	}

	public Good getGood() {
		return output.getGood();
	}
	
	@Override
	protected double calculateDividends(int day) {
		IStock wallet = getMoney();
		double dividend = Math.min(wallet.getAmount(), strategy.calcDividend(new Financials(wallet, inputs, output) {

			@Override
			public double getIdealCogs() {
				return prod.getCostOfMaximumProfit(new IPriceProvider() {

					@Override
					public double getPrice(Good good) {
						return Producer.this.getFactor(good).getPrice();
					}
				});
			}

		}));
		assert dividend <= wallet.getAmount();
		return dividend;
	}

	public IProductionFunction getProductionFunction() {
		return prod;
	}

	public void setProductionFunction(IProductionFunction prodFun) {
		this.prod = prodFun;
	}

	public double getOutputPrice() {
		return output.getPrice();
	}

	public Producer createNextGeneration(Endowment end, IProductionFunction prod) {
		return new Producer(getType(), end, prod, prices, strategy);
	}

	public Factor getFactor(Good good) {
		if (good.equals(this.output.getGood())) {
			return this.output;
		} else {
			for (InputFactor in : inputs) {
				if (in.getGood().equals(good)) {
					return in;
				}
			}
		}
		return null;
	}

	@Override
	public Good[] getInputs() {
		Good[] goods = new Good[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			goods[i] = inputs[i].getGood();
		}
		return goods;
	}

	@Override
	public Good getOutput() {
		return output.getGood();
	}

	@Override
	public Producer clone() {
		Producer klon = (Producer) super.clone();
		klon.output = output.duplicate(klon.getStock(output.getGood()));
		klon.inputs = new InputFactor[inputs.length];
		klon.strategy = strategy.duplicate();
		for (int i = 0; i < inputs.length; i++) {
			klon.inputs[i] = inputs[i].duplicate(klon.getStock(inputs[i].getGood()));
		}
		return klon;
	}
	
	@Override
	public String toString() {
		return "Firm with " + getMoney() + ", " + output + ", " + Arrays.toString(inputs);
	}

}
