package com.agentecon.finance;

import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Bid;
import com.agentecon.market.Price;

public class BidFin extends Bid {

	public BidFin(IStock wallet, Position stock, Price price, double amount) {
		super(wallet, stock, price, amount);
	}
	
	protected Position getStock(){
		return (Position)stock;
	}
	
	public double accept(IStock seller, Position target, double shares) {
		return super.accept(seller, target, Math.min(shares, target.getAmount()));
	}

	public Ticker getTicker() {
		return (Ticker) getGood();
	}

}
