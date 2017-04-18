// Created by Luzius on Apr 22, 2014

package com.agentecon.consumer;

import java.util.Collection;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.api.IConsumer;
import com.agentecon.api.IConsumerListener;
import com.agentecon.good.Good;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceFilter;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.sim.config.SimConfig;
import com.agentecon.stats.Numbers;

public class Consumer extends Agent implements IConsumer {
	
	private static final double SPENDING_FRACTION = 1.0;

	protected Good soldGood;
	private IUtility utility;
	private double lifetimeUtility;
	private ConsumerListeners listeners;

	public Consumer(String type, Endowment end, IUtility utility) {
		this(type, Integer.MAX_VALUE, end, utility);
	}

	public Consumer(String type, int maxAge, Endowment end, IUtility utility) {
		super(type, end);
		this.soldGood = end.getDaily()[0].getGood();
		this.utility = utility;
		this.lifetimeUtility = 0.0;
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

	public void trade(IPriceTakerMarket market) {
		Inventory inv = getInventory();
		inv = inv.hideRelative(getMoney().getGood(), 1.00 - SPENDING_FRACTION);
		trade(inv, market);
	}

	protected void trade(Inventory inv, IPriceTakerMarket market) {
		IStock money = inv.getStock(SimConfig.MONEY);
		boolean trading = true;
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
					double amountAcquired = offer.accept(money, s, excessStock);
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
					offer.accept(money, s, difference);
					trading = true;
				}
				pos++;
			}
		}
	}

	public final double consume() {
		return doConsume(getInventory());
	}

	protected double doConsume(Inventory inv) {
		double u = utility.consume(inv.getAll());
		assert!Double.isNaN(u);
		assert u >= 0.0;
		lifetimeUtility += u;
		return u;
	}

	@Override
	public Consumer clone() {
		Consumer klon = (Consumer) super.clone();
		return klon;
	}

	@Override
	public double getTotalExperiencedUtility() {
		return lifetimeUtility;
	}

	@Override
	public int getAge() {
		return 0;
	}
	
	@Override
	public boolean isRetired() {
		return false;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
