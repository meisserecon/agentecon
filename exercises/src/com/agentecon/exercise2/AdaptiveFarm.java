/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise2;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentId;
import com.agentecon.configuration.FarmingConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.firm.Financials;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.ExpectedRevenueBasedStrategy;
import com.agentecon.firm.decisions.FinanceDepartment;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.MarketingDepartment;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.ProducerListeners;

public class AdaptiveFarm extends Firm implements IProducer {

	private static final double SPENDING_FRACTION = 0.2; // let's spend 20% of our money every day

	private IProductionFunction prodFun;
	private ProducerListeners listeners;
	private MarketingDepartment marketing;
//	private IFirmDecisions strategy;
	private FinanceDepartment finance;

	public AdaptiveFarm(IAgentId id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun) {
		super(id, owner, new Endowment(money.getGood()));
		this.prodFun = prodFun;
		this.listeners = new ProducerListeners();
		this.marketing = new MarketingDepartment(getMoney(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
		this.finance = new FinanceDepartment(marketing.getFinancials(getInventory(), prodFun));
//		this.strategy = new ExpectedRevenueBasedStrategy(prodFun.getWeight(FarmingConfiguration.MAN_HOUR).weight);
		getStock(land.getGood()).absorb(land);
		getMoney().absorb(money);
	}

	@Override
	public void addProducerMonitor(IProducerListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public Good[] getInputs() {
		return prodFun.getInputs();
	}

	@Override
	public Good getOutput() {
		return prodFun.getOutput();
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		double budget = getMoney().getAmount() * SPENDING_FRACTION;
//		double budget = strategy.calcCogs(getMoney().getAmount(), prodFun.getCostOfMaximumProfit(getInventory(), marketing));
//		double optimal = prodFun.getCostOfMaximumProfit(getInventory(), marketing);
//		System.out.println("Budget " + budget + ", optimal: " + optimal);
		marketing.createOffers(market, this, budget);
	}

	@Override
	public void adaptPrices() {
		marketing.adaptPrices();
		// System.out.println("Adjusting price beliefs to " + marketing);
	}

	@Override
	public void produce() {
		Quantity[] inputs = getInventory().getQuantities(getInputs());
		Quantity produced = prodFun.produce(getInventory());
		listeners.notifyProduced(this, inputs, produced);
	}

	@Override
	protected double calculateDividends(int day) {
		return finance.calculateDividends();
//		double plainDividend = strategy.calcDividend(marketing.getFinancials(getInventory(), prodFun));
//		double fixedCosts = prodFun.getFixedCost(FarmingConfiguration.MAN_HOUR) * marketing.getPriceBelief(FarmingConfiguration.MAN_HOUR);
//		return plainDividend - fixedCosts;
		// return Math.max(0, getMoney().getAmount() - workingCapital); //
		// return everyhing above what was initially provided
	}

}
