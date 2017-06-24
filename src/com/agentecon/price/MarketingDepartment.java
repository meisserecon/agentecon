package com.agentecon.price;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.InputFactor;
import com.agentecon.firm.OutputFactor;
import com.agentecon.firm.sensor.SensorInputFactor;
import com.agentecon.firm.sensor.SensorOutputFactor;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.IPriceProvider;

public class MarketingDepartment implements IPriceProvider {
	
	private IStock money;
	private InputFactor input;
	private OutputFactor output;
	
	public MarketingDepartment(IStock money, IStock input, IStock output) {
		this.money = money;
		this.input = new SensorInputFactor(input, new ExpSearchBelief());
		this.output = new SensorOutputFactor(output, new ExpSearchBelief());
	}

	@Override
	public double getPriceBelief(Good good) {
		if (input.getGood().equals(good)){
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

}
