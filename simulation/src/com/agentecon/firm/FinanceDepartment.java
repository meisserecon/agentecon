/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.Weight;
import com.agentecon.firm.decisions.ExpectedRevenueBasedStrategy;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.firm.decisions.IFirmDecisions;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IDiscountRate;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.PriceUnknownException;

public class FinanceDepartment extends ExpectedRevenueBasedStrategy implements IFirmDecisions {

	private static final double DELTA = 0.1;

	private CobbDouglasProduction prodFun;
	private double prevInvestment = 0.0;
	private IDiscountRate discountRate;

	public FinanceDepartment(CobbDouglasProduction prodFun, IDiscountRate iDiscountRate) {
		super(prodFun.getReturnsToScaleExcludingCapital());
		this.prodFun = prodFun;
		this.discountRate = iDiscountRate;
	}

	public void invest(IAgent agent, Inventory inv, IFinancials financials, IPriceTakerMarket market) {
		double revenue = financials.getExpectedRevenue();
		double investments = 0.0;
		double timeValueFactor = 1 / discountRate.getCurrentDiscountRate();
		for (Weight input : prodFun.getInputWeigths()) {
			if (input.capital) {
				Good inputGood = input.good;
				investments += invest(agent, inv.getMoney(), inv.getStock(inputGood), revenue * input.weight * timeValueFactor, market);
			}
		}
		this.prevInvestment = investments;
	}

	private double invest(IAgent agent, IStock money, IStock input, double targetLandValue, IPriceTakerMarket market) {
		double invested = considerBuying(agent, money, input, targetLandValue, market);
		double divested = considerSelling(agent, money, input, targetLandValue, market);
		return invested - divested;
	}

	protected double considerBuying(IAgent agent, IStock money, IStock input, double targetLandValue, IPriceTakerMarket market) {
		double invested = 0.0;
		IOffer offer = market.getOffer(input.getGood(), true);
		if (offer != null) {
			double additionalLandNeeded = targetLandValue - offer.getPrice().getPrice() * input.getAmount();
			while (additionalLandNeeded > 0.0) {
				double moneyBefore = money.getAmount();
				offer.accept(agent, money, input, new Quantity(input.getGood(), additionalLandNeeded / offer.getPrice().getPrice()));
				invested += moneyBefore - money.getAmount();
				offer = market.getOffer(input.getGood(), true);
				additionalLandNeeded = targetLandValue - offer.getPrice().getPrice() * input.getAmount();
			}
		}
		return invested;
	}

	protected double considerSelling(IAgent agent, IStock money, IStock input, double targetLandValue, IPriceTakerMarket market) {
		double divested = 0.0;
		IOffer offer = market.getOffer(input.getGood(), false);
		if (offer != null) {
			double excessLand = input.getAmount() - targetLandValue / offer.getPrice().getPrice();
			while (excessLand > 0.0) {
				double moneyBefore = money.getAmount();
				offer.accept(agent, money, input, new Quantity(input.getGood(), excessLand / offer.getPrice().getPrice()));
				divested += money.getAmount() - moneyBefore;
				offer = market.getOffer(input.getGood(), false);
				excessLand = input.getAmount() - targetLandValue / offer.getPrice().getPrice();
			}
		}
		return divested;
	}

	/**
	 * Returns whether owning more land would increase the return on capital
	 */
	protected boolean shouldInvest(Quantity laborInput, Quantity availableLand) throws PriceUnknownException {
		Quantity productionNow = prodFun.calculateOutput(availableLand, laborInput);
		Quantity moreLand = new Quantity(availableLand.getGood(), availableLand.getAmount() + DELTA);
		Quantity productionWithMoreLand = prodFun.calculateOutput(moreLand, laborInput);
		double marginalProductivity = (productionWithMoreLand.getAmount() - productionNow.getAmount()) / DELTA;
		double overallProductivity = productionNow.getAmount() / availableLand.getAmount();
		return marginalProductivity > overallProductivity;
	}

	@Override
	public double calcDividend(IFinancials metrics) {
		double dividends = super.calcDividend(metrics);
		// subtract investments from dividends, but do not add divestments in order to shrink slowly after doing so
		return dividends - Math.max(0, prevInvestment);
	}

	@Override
	public FinanceDepartment duplicate() {
		return new FinanceDepartment(prodFun, discountRate);
	}

}
