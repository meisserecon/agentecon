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
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.FarmingConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.ExpectedRevenueBasedStrategy;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.price.MarketingDepartment;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.production.ProducerListeners;

public class AdaptiveFarm extends Firm implements IProducer {

	private static final double SPENDING_FRACTION = 0.2; // let's spend 20% of our money every day
	private static final double MINIMUM_TARGET_INPUT = 14;

	private IProductionFunction prodFun;
	private ProducerListeners listeners;
	private MarketingDepartment marketing;
	private IFirmDecisions strategy;
//	private FinanceDepartment finance;

	public AdaptiveFarm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IMarketStatistics stats) {
		super(id, owner, new Endowment(money.getGood()));
		this.prodFun = prodFun;
		this.listeners = new ProducerListeners();
		this.marketing = new MarketingDepartment(getMoney(), stats, getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
//		this.finance = new FinanceDepartment(marketing.getFinancials(getInventory(), prodFun));
		this.strategy = new ExpectedRevenueBasedStrategy(prodFun.getWeight(FarmingConfiguration.MAN_HOUR).weight);
		getStock(land.getGood()).absorb(land);
		getMoney().absorb(money);
		assert getMoney().getAmount() > 0;
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
		double budget = calculateBudget();
		marketing.createOffers(market, this, budget);
	}
	
	private double calculateBudget(){
		double defaultBudget = getMoney().getAmount() * SPENDING_FRACTION;
		double minimumReasonableSpending = MINIMUM_TARGET_INPUT * marketing.getPriceBelief(FarmingConfiguration.MAN_HOUR);
		if (getMoney().getAmount() < minimumReasonableSpending){
			return 0;
		} else {
			return Math.max(defaultBudget, minimumReasonableSpending);
		}
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
	
	private IFinancials getFinancials(){
		return marketing.getFinancials(getInventory(), prodFun);
	}

	@Override
	protected double calculateDividends(int day) {
		try {
			double fixedCosts = prodFun.getFixedCosts(marketing);
			return strategy.calcDividend(getFinancials());
		} catch (PriceUnknownException e) {
			return 0.0; // we don't know what reasonable prices are, better not pay a dividend until we know more
		}
	}
	
	private int daysWithoutProfit = 0;
	
	@Override
	public boolean wantsBankruptcy(IStatistics stats){
		double profits = getFinancials().getProfits();
		if (profits <= 0){
			daysWithoutProfit++;
		} else {
			daysWithoutProfit = 0;
		}
		return daysWithoutProfit > 5 && stats.getRandomNumberGenerator().nextDouble() < 0.2;
//		return getMoney().getAmount() < 10.0; // we ran out of money, go bankrupt
	}

}
