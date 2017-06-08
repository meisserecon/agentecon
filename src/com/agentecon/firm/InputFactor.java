package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;
import com.agentecon.price.IPrice;

public class InputFactor extends Factor {

	public InputFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	protected AbstractOffer newOffer(IAgent owner, IStock money, double p, double planned) {
		return new Bid(owner, money, getStock(), new Price(getGood(), p), planned);
	}
	
	@Override
	public void createOffers(IPriceMakerMarket market, IAgent owner, IStock money, double budget) {
		super.createOffers(market, owner, money, budget / price.getPrice());  // NOT getPrice() as overridden in subclass
	}
	
	public InputFactor duplicate(IStock stock){
		return new InputFactor(stock, price);
	}

}
