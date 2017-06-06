package com.agentecon.finance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.BestPriceMarket;
import com.agentecon.market.Bid;
import com.agentecon.market.MarketListeners;
import com.agentecon.util.InstantiatingHashMap;

public class DailyStockMarket implements IStockMarket {

	private Random rand;
	private MarketListeners listeners;
	private HashMap<Ticker, BestPriceMarket> market;

	public DailyStockMarket(MarketListeners listeners, Random rand) {
		this.rand = rand;
		this.listeners = listeners;
		this.market = new InstantiatingHashMap<Ticker, BestPriceMarket>() {

			@Override
			protected BestPriceMarket create(Ticker key) {
				return new BestPriceMarket(key);
			}
		};
	}

	@Override
	public void offer(Bid bid) {
		bid.setListener(listeners);
		this.market.get(bid.getGood()).offer(bid);
	}

	@Override
	public void offer(Ask ask) {
		ask.setListener(listeners);
		this.market.get(ask.getGood()).offer(ask);
	}

	@Override
	public Ticker findAnyAsk(List<Ticker> preferred, boolean marketCapWeight) {
		while (preferred.size() > 0){
			int choice = rand.nextInt(preferred.size());
			Ticker t = preferred.get(choice);
			if (hasAsk(t)){
				return t;
			} else {
				preferred.remove(choice);
			}
		}
		if (marketCapWeight){
			return findMarketCapWeightedRandomAsk();
		} else {
			return findRandomAsk();
		}
	}

	private Ticker findRandomAsk() {
		ArrayList<Ticker> asks = new ArrayList<>();
		for (BestPriceMarket bpm: market.values()){
			Ask ask = bpm.getAsk();
			if (ask != null){
				asks.add((Ticker) ask.getGood());
			}
		}
		int size = asks.size();
		return size == 0 ? null : asks.get(rand.nextInt(size));
	}

	protected Ticker findMarketCapWeightedRandomAsk() {
		// TEMP improve performance
		ArrayList<Ask> list = new ArrayList<>();
		double total = 0.0;
		for (BestPriceMarket market: market.values()){
			Ask ask = market.getAsk();
			if (ask != null){
				list.add(ask);
				total += ask.getPrice().getPrice();
			}
		}
		double selection = rand.nextDouble() * total;
		double pos = 0.0;
		for (Ask a: list){
			pos += a.getPrice().getPrice();
			if (pos >= selection){
				return (Ticker) a.getGood();
			}
		}
		
		return null;
	}

	@Override
	public Position buy(Ticker ticker, Position existing, IStock wallet, double budget) {
		AskFin ask = getAsk(ticker);
		if (ask != null) {
			return ask.accept(wallet, existing, budget);
		} else {
			return existing;
		}
	}

	public Ticker findHighestBid(Collection<Ticker> keySet) {
		BidFin highest = null;
		for (Ticker ticker : keySet) {
			BidFin bid = getBid(ticker);
			if (bid != null) {
				if (highest == null || bid.getPrice().getPrice() > highest.getPrice().getPrice()) {
					highest = bid;
				}
			}
		}
		return highest == null ? null : highest.getTicker();
	}

	@Override
	public double sell(Position pos, IStock wallet, double shares) {
		BidFin bid = getBid(pos.getTicker());
		if (bid != null) {
			return bid.accept(wallet, pos, shares);
		} else {
			return 0.0;
		}
	}

	@Override
	public AskFin getAsk(Ticker ticker) {
		BestPriceMarket best = market.get(ticker);
		return (AskFin) best.getAsk();
	}

	@Override
	public BidFin getBid(Ticker ticker) {
		BestPriceMarket best = market.get(ticker);
		return (BidFin) best.getBid();
	}

	@Override
	public boolean hasBid(Ticker ticker) {
		return getBid(ticker) != null;
	}

	@Override
	public boolean hasAsk(Ticker ticker) {
		return getAsk(ticker) != null;
	}
	
	public String getTradingStats() {
		int asks = 0, bids = 0;
		for (BestPriceMarket bpm: market.values()){
			if (bpm.getAsk() != null){
				asks++;
			}
			if (bpm.getBid() != null){
				bids++;
			}
		}
		return asks + "/" + market.size() + " asks and " + bids + "/" + market.size() + " bids left";
	}

}
