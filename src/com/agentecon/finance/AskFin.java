package com.agentecon.finance;

import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.Price;

public class AskFin extends Ask {

	public AskFin(IStock wallet, Position stock, Price price, double amount) {
		super(wallet, stock, price, amount);
	}
	
	protected Position getStock(){
		return (Position)stock;
	}
	
	public Position accept(IStock payer, Position target, double budget) {
		if (target == null){
			target = getStock().split();
		}
		super.accept(payer, target, budget / getPrice().getPrice());
		return target;
	}

	public Ticker getTicker() {
		return (Ticker) getGood();
	}

}
