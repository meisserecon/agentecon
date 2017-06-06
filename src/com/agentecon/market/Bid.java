// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.goods.IStock;
import com.agentecon.util.Numbers;

public class Bid extends AbstractOffer {
	
	public Bid(IStock wallet, IStock stock, Price price, double quantity){
		super(wallet, stock, price, quantity);
		assert wallet.getAmount() - getAmount() * getPrice().getPrice() >= -Numbers.EPSILON;
		assert quantity > 0;
	}
	
	@Override
	public double getAmount(){
		// in case money got removed from wallet elsewhere
		return Math.min(super.getAmount(), wallet.getAmount() / getPrice().getPrice());
	}
	
	@Override
	public double accept(IStock seller, IStock sellerStock, double targetAmount){
		assert sellerStock.getAmount() >= targetAmount;
		double amount = Math.min(targetAmount, getAmount());
		assert amount >= 0;
		double total = amount * getPrice().getPrice();
		transfer(seller, -total, sellerStock, amount);
		return amount;
	}
	
	@Override
	public int compareTo(AbstractOffer o) {
		return -super.compareTo(o);
	}

	@Override
	public boolean isBid() {
		return true;
	}
	
	@Override
	public IOffer getBetterOne(IOffer other) {
		return getPrice().isAbove(other.getPrice()) ? this : other;
	}
	
	public void match(Ask ask) {
		if (!ask.getPrice().isAbove(getPrice())){
			double m1 = wallet.getAmount();
			double amount = ask.accept(wallet, stock, getAmount());
			assert amount >= 0;
			double income = wallet.getAmount() - m1;
			doStats(income, amount);
		}
	}

}
