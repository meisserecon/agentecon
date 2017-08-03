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
import com.agentecon.firm.decisions.CostOrientedStrategy;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Quantity;
import com.agentecon.learning.MarketingDepartment;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.ProducerListeners;

public class ExperimentalFarm extends Firm implements IProducer {

	private int creation;
	private IProductionFunction prodFun;
	private ProducerListeners listeners;
	private MarketingDepartment marketing;
	private IFirmDecisions strategy;

	public ExperimentalFarm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, new Endowment(money.getGood()));
		this.prodFun = prodFun;
		this.listeners = new ProducerListeners();
		this.marketing = new MarketingDepartment(getMoney(), stats.getGoodsMarketStats(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
		this.strategy = new CostOrientedStrategy();
		this.creation = stats.getDay();
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
		double budget = strategy.calcCogs(getFinancials());
		marketing.createOffers(market, this, budget);
	}

	@Override
	public void adaptPrices() {
		marketing.adaptPrices();
	}

	@Override
	public void produce() {
		Quantity[] inputs = getInventory().getQuantities(getInputs());
		Quantity produced = prodFun.produce(getInventory());
		listeners.notifyProduced(this, inputs, produced);
	}

	private IFinancials getFinancials() {
		return marketing.getFinancials(getInventory(), prodFun);
	}

	@Override
	protected double calculateDividends(int day) {
		if (day > creation + 20) {
			double div = strategy.calcDividend(getFinancials());
			return div;
		} else {
			return 0.0;
		}
	}

	private int daysWithoutProfit = 0;

	@Override
	public boolean wantsBankruptcy(IStatistics stats) {
		double profits = getFinancials().getProfits();
		if (profits <= 0) {
			daysWithoutProfit++;
		} else {
			daysWithoutProfit = 0;
		}
		return daysWithoutProfit > 5 && stats.getRandomNumberGenerator().nextDouble() < 0.2;
		// return getMoney().getAmount() < 10.0; // we ran out of money, go bankrupt
	}

}
