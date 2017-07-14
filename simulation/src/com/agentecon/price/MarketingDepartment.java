package com.agentecon.price;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Financials;
import com.agentecon.firm.InputFactor;
import com.agentecon.firm.OutputFactor;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.firm.sensor.SensorInputFactor;
import com.agentecon.firm.sensor.SensorOutputFactor;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IMarketStatistics;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.AbstractPriceProvider;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

public class MarketingDepartment extends AbstractPriceProvider {

	private IStock money;
	private InputFactor input;
	private OutputFactor output;

	public MarketingDepartment(IStock money, IMarketStatistics stats, IStock input, IStock output) {
		this.money = money;
		this.input = new SensorInputFactor(input, getPriceBelief(stats, input.getGood()));
		this.output = new SensorOutputFactor(output, getPriceBelief(stats, output.getGood()));
	}

	private IBelief getPriceBelief(IMarketStatistics stats, Good good) {
		try {
			return new ExpSearchBelief(stats.getPriceBelief(good));
		} catch (PriceUnknownException e) {
			// price not known, use default belief
			return new ExpSearchBelief();
		}
	}

	@Override
	public double getPriceBelief(Good good) {
		if (input.getGood().equals(good)) {
			return input.getPrice();
		} else {
			assert output.getGood().equals(good);
			return output.getPrice();
		}
	}

	public void createOffers(IPriceMakerMarket market, IAgent initiator, double budget) {
		input.createOffers(market, initiator, money, budget);
		output.createOffers(market, initiator, money, output.getStock().getAmount());
	}

	public void adaptPrices() {
		input.adaptPrice();
		output.adaptPrice();
	}

	@Override
	public String toString() {
		return "Input " + input + ", output " + output;
	}

	public IFinancials getFinancials(final Inventory inv, IProductionFunction prodFun) {
		return new Financials(money, output, input) {

			@Override
			public double getIdealCogs() throws PriceUnknownException {
				return prodFun.getCostOfMaximumProfit(inv, MarketingDepartment.this);
			}

		};
	}

}
