package com.agentecon.finance;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.good.IStock;

public class Portfolio implements Cloneable {

	protected IStock wallet;
	private double dividendsAfterTax;
	protected HashMap<Ticker, Position> inv;

	public Portfolio(IStock money) {
		this.wallet = money;
		this.inv = new HashMap<>();
		this.dividendsAfterTax = 0.0;
	}

	public void absorb(Portfolio other) {
		wallet.absorb(other.wallet);
		for (Position p : other.inv.values()) {
			Position existing = inv.get(p.getTicker());
			if (existing == null) {
				inv.put(p.getTicker(), p);
			} else {
				p.dispose(existing);
			}
		}
	}

	public void addPosition(Position pos) {
		if (pos != null) {
			Position prev = inv.put(pos.getTicker(), pos);
			if (prev != null && prev != pos) {
				prev.dispose(pos);
			}
		}
	}

	public Collection<Position> getPositions() {
		return inv.values();
	}

	public Position getPosition(Ticker ticker) {
		return inv.get(ticker);
	}

	public void disposePosition(Ticker t) {
		Position p = inv.remove(t);
		if (p != null) {
			p.dispose();
		}
	}

	public void dispose() {
		this.inv.clear();
	}

	public boolean hasPositions() {
		return inv.size() > 0;
	}

	public void collectDividends() {
		this.collectDividends(null);
	}

	public void collectDividends(ITax tax) {
		double money = wallet.getAmount();
		for (Position p : inv.values()) {
			p.collectDividend(wallet);
		}
		double dividends = wallet.getAmount() - money;
		if (tax != null) {
			tax.collect(wallet, dividends);
		}
		this.dividendsAfterTax = wallet.getAmount() - money;
	}

	public double getLatestDividendIncome() {
		return dividendsAfterTax;
	}

	public double getCash() {
		return wallet.getAmount();
	}

	public Portfolio clone(IStock wallet) {
		try {
			Portfolio klon = (Portfolio) super.clone();
			klon.wallet = wallet;
			// TODO: duplicate positions
			return klon;
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return wallet + ", " + inv.values();
	}

}
