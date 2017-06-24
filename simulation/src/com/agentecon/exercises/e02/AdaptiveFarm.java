/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercises.e02;

import com.agentecon.agent.Endowment;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IShareholder;
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
	
	public AdaptiveFarm(IShareholder owner, IStock money, IStock land, IProductionFunction prodFun) {
		super(owner, new Endowment(money.getGood()));
		this.prodFun = prodFun;
		this.listeners = new ProducerListeners();
		this.marketing = new MarketingDepartment(getMoney(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
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

	@Override
	protected double calculateDividends(int day) {
		return Math.max(0, getMoney().getAmount() - 104); // return everyhing above 100
	}

}
