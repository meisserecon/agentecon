// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.TradingPortfolio;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceFilter;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.util.Numbers;

public class Consumer extends Agent implements IConsumer, IShareholder {

	private int age;
	private Good soldGood;
	private IUtility utility;
	protected TradingPortfolio portfolio;
	protected ConsumerListeners listeners;

	public Consumer(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end);
		this.age = 0;
		this.soldGood = end.getDaily()[0].getGood();
		this.utility = utility;
		this.portfolio = new TradingPortfolio(getMoney());
		this.listeners = new ConsumerListeners();
	}

	public void addListener(IConsumerListener listener) {
		this.listeners.add(listener);
	}

	public IUtility getUtilityFunction() {
		return utility;
	}

	public void managePortfolio(IStockMarket stocks) {
	}

	public void tradeGoods(IPriceTakerMarket market) {
		Inventory inv = getInventory();
		if (isRetired()) {
			inv = inv.hide(soldGood); // cannot work any more, hide hours
		}
		trade(inv, market);
	}

	protected void trade(Inventory inv, IPriceTakerMarket market) {
		boolean trading = true;
		double spendings = 0.0;
		while (trading) {
			trading = false;
			IStock money = inv.getMoney();
			Collection<IOffer> offers = market.getOffers(new IPriceFilter() {

				@Override
				public boolean isAskPricePreferred(Good good) {
					return !good.equals(soldGood);
				}

				@Override
				public boolean isOfInterest(Good good) {
					if (soldGood.equals(good)) {
						return !isRetired();
					} else {
						return utility.isValued(good);
					}
				}
			});

			double[] allocs = utility.getOptimalAllocation(inv, offers);
			assert allocs.length == offers.size();

			boolean completedSales = true;
			int pos = 0;
			for (IOffer offer : offers) {
				IStock s = inv.getStock(offer.getGood());
				double excessStock = s.getAmount() - allocs[pos];
				// double excessStock = Math.max(s.getAmount() - allocs[pos],
				// s.getAmount() - 19); // work at least 5 hours
				if (excessStock > Numbers.EPSILON && offer.getGood() == soldGood) {
					double amountAcquired = offer.accept(this, money, s, excessStock);
					completedSales &= amountAcquired == excessStock;
					trading = true;
				}
				pos++;
			}
			if (!completedSales) {
				continue;
			}
			pos = 0;
			for (IOffer offer : offers) {
				IStock s = inv.getStock(offer.getGood());
				double difference = allocs[pos] - s.getAmount();
				if (difference > Numbers.EPSILON && offer.getGood() != soldGood && !money.isEmpty()) {
					double moneyAmount = money.getAmount();
					offer.accept(this, money, s, difference);
					spendings += (moneyAmount - money.getAmount());
					trading = true;
				}
				pos++;
			}
		}
		notifySpent(spendings);
	}
	
	public void workAtLeast(IPriceTakerMarket market, double minimumWorkAmount) {
		if (isRetired()){
			// still not working
		} else {
			IStock timeLeft = getStock(soldGood);
			double endowment = getDailyEndowment(soldGood);
			double worked = endowment - timeLeft.getAmount();
			while (worked < minimumWorkAmount){
				IOffer offer = market.getOffer(soldGood, true);
				if (offer == null){
					break;
				} else {
					offer.accept(this, getMoney(), timeLeft, minimumWorkAmount - worked);
				}
				worked = endowment - timeLeft.getAmount();
			}
		}
	}

	protected void notifySpent(double spendings) {
	}

	public double consume() {
		Inventory inv = getInventory();
		double u = utility.consume(inv.getAll());
		listeners.notifyConsuming(this, getAge(), getInventory(), u);
		assert !Double.isNaN(u);
		assert u >= 0.0;
		return u;
	}

	public boolean isMortal() {
		return false;
	}

	public Inventory age(Portfolio inheritance) {
		return null;
	}

	public boolean isRetired() {
		return false;
	}

	@Override
	public int getAge() {
		return age;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public Consumer clone() {
		Consumer klon = (Consumer) super.clone();
		klon.portfolio = portfolio.clone(klon.getMoney());
		return klon;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
