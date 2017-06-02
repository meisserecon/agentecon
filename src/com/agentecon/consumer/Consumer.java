// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
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
import com.agentecon.util.MovingAverage;
import com.agentecon.util.Numbers;

public class Consumer extends Agent implements IConsumer, IShareholder {

	private int age, maxAge;
	protected Good soldGood;
	private IUtility utility;
	private TradingPortfolio portfolio;
	private MovingAverage dailySpendings;
	private double savingsTarget;
	private ConsumerListeners listeners;

	public Consumer(String type, Endowment end, IUtility utility) {
		this(type, Integer.MAX_VALUE, end, utility);
	}

	public Consumer(String type, int maxAge, Endowment end, IUtility utility) {
		super(type, end);
		this.maxAge = maxAge;
		this.soldGood = end.getDaily()[0].getGood();
		this.utility = utility;
		this.dailySpendings = new MovingAverage(0.95);
		this.portfolio = new TradingPortfolio(getMoney());
		this.listeners = new ConsumerListeners();
	}

	public void addListener(IConsumerListener listener) {
		this.listeners.add(listener);
	}

	public IUtility getUtilityFunction() {
		return utility;
	}

	public void setUtilityFunction(IUtility utility) {
		this.utility = utility;
	}

	public void managePortfolio(IStockMarket stocks) {
		if (isMortal()) {
			if (isRetired()) {
				int daysLeft = maxAge - age + 1;
				double amount = portfolio.sell(stocks, 1.0 / daysLeft);
				listeners.notifyDivested(this, amount);
			} else {
				double invest = dailySpendings.getAverage() / maxAge * (maxAge - getRetirementAge());
				invest(stocks, invest);
			}
		} else {
			double invest = dailySpendings.getAverage() * 0.2;
			invest(stocks, invest);
		}
	}

	private void invest(IStockMarket stocks, double invest) {
		double dividendIncome = portfolio.getLatestDividendIncome();
		if (dividendIncome < invest) {
			savingsTarget = invest - dividendIncome;
			invest = Math.min(getMoney().getAmount(), invest);
		} else {
			savingsTarget = 0.0;
		}
		double amount = portfolio.invest(stocks, invest);
		listeners.notifyInvested(this, amount);
	}

	public void maximizeUtility(IPriceTakerMarket market) {
		Inventory inv = getInventory();
		if (isRetired()) {
			inv = inv.hide(soldGood); // cannot work any more, hide hours
		}
		if (savingsTarget > 0.0) {
			inv = inv.hide(getMoney().getGood(), Math.min(savingsTarget, dailySpendings.getAverage() / 2));
		}
		trade(inv, market);
	}

	protected void trade(Inventory inv, IPriceTakerMarket market) {
		boolean trading = true;
		double spendings = 0.0;
		while (trading) {
			trading = false;
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
				// double excessStock = Math.max(s.getAmount() - allocs[pos], s.getAmount() - 19); // work at least 5 hours
				if (excessStock > Numbers.EPSILON && offer.getGood() == soldGood) {
					double amountAcquired = offer.accept(getMoney(), s, excessStock);
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
				if (difference > Numbers.EPSILON && offer.getGood() != soldGood && !getMoney().isEmpty()) {
					offer.accept(getMoney(), s, difference);
					spendings += difference * offer.getPrice().getPrice();
					trading = true;
				}
				pos++;
			}
		}
		dailySpendings.add(spendings);
	}

	public final double consume() {
		Inventory inv = getInventory();
		double u = utility.consume(inv.getAll());
		listeners.notifyConsuming(this, getAge(), getInventory(), u);
		assert!Double.isNaN(u);
		assert u >= 0.0;
		return u;
	}

	public boolean isMortal() {
		return maxAge < Integer.MAX_VALUE;
	}

	public Inventory age(Portfolio inheritance) {
		if (age == getRetirementAge()) {
			listeners.notifyRetiring(this, age);
		}
		this.age++;
		if (age > maxAge) {
			inheritance.absorb(portfolio);
			return super.dispose();
		} else {
			return null;
		}
	}

	public boolean isRetired() {
		return age > getRetirementAge();
	}

	private int getRetirementAge() {
		return maxAge / 5 * 3;
	}

	@Override
	public Consumer clone() {
		Consumer klon = (Consumer) super.clone();
		klon.dailySpendings = dailySpendings.clone();
		klon.portfolio = portfolio.clone(klon.getMoney());
		return klon;
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

}
