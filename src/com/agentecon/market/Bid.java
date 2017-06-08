// Created by Luzius on Apr 28, 2014

package com.agentecon.market;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.IStock;
import com.agentecon.util.Numbers;

public class Bid extends AbstractOffer {
	
	public Bid(IAgent initiator, IStock wallet, IStock stock, Price price, double quantity){
		super(initiator, wallet, stock, price, quantity);
		assert wallet.getAmount() - getAmount() * getPrice().getPrice() >= -Numbers.EPSILON;
		assert quantity > 0;
	}
	
	@Override
	public double getAmount(){
		// in case money got removed from wallet elsewhere
		return Math.min(super.getAmount(), wallet.getAmount() / getPrice().getPrice());
	}
	
	@Override
	public double accept(IAgent acceptingAgent, IStock seller, IStock sellerStock, double targetAmount){
		assert sellerStock.getAmount() >= targetAmount;
		double amount = Math.min(targetAmount, getAmount());
		assert amount >= 0;
		double total = amount * getPrice().getPrice();
		transfer(acceptingAgent, seller, -total, sellerStock, amount);
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
			double amount = ask.accept(getOwner(), wallet, stock, getAmount());
			assert amount >= 0;
		}
	}

}
