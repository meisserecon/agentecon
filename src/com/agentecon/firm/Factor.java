package com.agentecon.firm;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.price.IBelief;
import com.agentecon.util.Numbers;

public abstract class Factor {

	protected IStock stock;
	protected IBelief price;
	protected AbstractOffer prevOffer;

	public Factor(IStock stock, IBelief price) {
		assert stock != null;
		assert price != null;
		this.stock = stock;
		this.price = price;
	}

	public void adaptPrice() {
		if (prevOffer != null) {
			price.adapt(shouldIncrease());
		}
	}

	protected boolean shouldIncrease() {
		boolean success = prevOffer.isUsed();
		return prevOffer.isBid() ? !success : success;
	}

	public void createOffers(IPriceMakerMarket market, IAgent owner, IStock money, double amount) {
		prevOffer = newOffer(owner, money, price.getPrice(), amount);
		if (prevOffer.isBid()) {
			market.offer((Bid) prevOffer);
		} else {
			market.offer((Ask) prevOffer);
		}
	}

	protected abstract AbstractOffer newOffer(IAgent owner, IStock money, double p, double planned);

	public double getVolume() {
		return prevOffer == null ? 0.0 : prevOffer.getTransactionVolume();
	}

	public double getQuantity() {
		return prevOffer == null ? 0.0 : prevOffer.getTransactionVolume() / prevOffer.getPrice().getPrice();
	}

	public boolean isObtainable() {
		return !price.isProbablyUnobtainable();
	}

	protected double getCurrentSuccessRate() {
		return prevOffer.isUsed() ? 1.0 : 0.0;
	}

	public final Good getGood() {
		return stock.getGood();
	}

	public final IStock getStock() {
		return stock;
	}

	public double getPrice() {
		return price.getPrice();
	}

	public String toString() {
		return stock + " at " + price;
	}

}
