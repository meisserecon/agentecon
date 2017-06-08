package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Factor;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.market.AbstractOffer;
import com.agentecon.market.Price;
import com.agentecon.price.IPrice;

public class CeilingFactor extends Factor {

	public CeilingFactor(IStock stock, IPrice price) {
		super(stock, price);
	}

	public void adapt(double min) {
		if (prevOffer != null) {
			price.adaptWithFloor(shouldIncrease(), min);
		}
	}

	@Override
	protected AbstractOffer newOffer(IAgent owner, IStock money, double price, double amount) {
		return new AskFin(owner, money, (Position) stock, new Price(getGood(), price), amount);
	}

}
