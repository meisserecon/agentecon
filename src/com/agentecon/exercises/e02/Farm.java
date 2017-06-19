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
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.ProducerListeners;

public class Farm extends Firm implements IProducer {

	private static final double POTATOE_PRICE = 1.0; // let's normalize potatoes
	private static final double SPENDING_FRACTION = 0.2; // let's spend 20% of our money every day
	private static final double LABOR_PER_DAY = 20; // try to acquire 20 man-hours per day
	
	private IProductionFunction prodFun;
	private ProducerListeners listeners;
	
	public Farm(IShareholder owner, IStock money, IStock land, IProductionFunction prodFun) {
		super(owner, new Endowment(money.getGood()));
		this.prodFun = prodFun;
		this.listeners = new ProducerListeners();
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
		IStock output = getStock(getOutput());
		if (!output.isEmpty()){
			market.offer(new Ask(this, getMoney(), output, POTATOE_PRICE));
		}
		double spendings = getMoney().getAmount() * SPENDING_FRACTION;
		IStock labor = getStock(getInputs()[0]);
		Price wage = new Price(labor.getGood(), spendings / LABOR_PER_DAY);
		market.offer(new Bid(this, getMoney(), labor, wage, LABOR_PER_DAY));
	}
	
	@Override
	public void adaptPrices() {
	}

	@Override
	public void produce() {
		Quantity[] inputs = getInventory().getQuantities(getInputs());
		Quantity produced = prodFun.produce(getInventory());
		listeners.notifyProduced(this, inputs, produced);
	}

	@Override
	protected double calculateDividends(int day) {
		return Math.max(0, getMoney().getAmount() - 100); // return everyhing above 100
	}

}
