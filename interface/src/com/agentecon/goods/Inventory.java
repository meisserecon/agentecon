package com.agentecon.goods;

import java.util.Collection;
import java.util.HashMap;

public class Inventory {

	private final Good money;
	private final HashMap<Good, IStock> inv;

	public Inventory(Good money, IStock... initial) {
		this.money = money;
		this.inv = new HashMap<Good, IStock>();
		for (IStock s : initial) {
			inv.put(s.getGood(), s);
		}
	}

	public Inventory(Good money, Collection<IStock> initial, boolean duplicate) {
		this.money = money;
		this.inv = new HashMap<Good, IStock>();
		for (IStock s : initial) {
			inv.put(s.getGood(), duplicate ? s.duplicate() : s);
		}
	}
	
	public IStock getMoney(){
		return getStock(money);
	}

	/**
	 * Returns a delegate of this inventory that hides amount of good. Changes to the delegate will also be reflected in this inventory.
	 */
	public Inventory hide(Good good, double amount) {
		Inventory clone = new Inventory(money, inv.values(), false) {
			protected IStock createStock(Good type) {
				return Inventory.this.getStock(type);
			}
		};
		clone.inv.put(good, getStock(good).hide(amount));
		return clone;
	}
	
	/**
	 * Returns a delegate of this inventory that hides the given fraction of the good including the same fraction of the income.
	 * Changes to the delegate will also be reflected in this inventory.
	 */
	public Inventory hideRelative(Good good, double fraction) {
		Inventory clone = new Inventory(money, inv.values(), false) {
			protected IStock createStock(Good type) {
				return Inventory.this.getStock(type);
			}
		};
		clone.inv.put(good, getStock(good).hideRelative(fraction));
		return clone;
	}

	/**
	 * Hides all deposits of that good. Additions will still be reflected in the underlying inventory.
	 */
	public Inventory hide(Good good) {
		IStock stock = inv.get(good);
		return hide(good, stock.getAmount());
	}

	public Collection<IStock> getAll() {
		return inv.values();
	}

	public IStock getStock(Good type) {
		IStock s = inv.get(type);
		if (s == null) {
			s = createStock(type);
			inv.put(type, s);
		}
		return s;
	}

	protected IStock createStock(Good type) {
		return new Stock(type);
	}
	
	public void absorb(Inventory other) {
		for (IStock s: other.inv.values()){
			receive(s);
		}
	}

	public void receive(IStock[] daily) {
		for (IStock s : daily) {
			receive(s);
		}
	}

	public void receive(IStock s) {
		IStock present = getStock(s.getGood());
		present.absorb(s);
	}
	
	public Inventory duplicate(){
		return new Inventory(money, inv.values(), true);
	}

	public void deprecate() {
		for (IStock s : inv.values()) {
			s.deprecate();
		}
	}
	
	@Override
	public String toString() {
		String list = null;
		for (IStock s : inv.values()) {
			if (!s.isEmpty()) {
				if (list == null) {
					list = s.toString();
				} else {
					list += ", " + s;
				}
			}
		}
		if (list == null) {
			return "Empty inventory";
		} else {
			return "Inventory with " + list;
		}
	}

	public Quantity[] getQuantities(Good[] goods) {
		Quantity[] inputAmounts = new Quantity[goods.length];
		for (int i = 0; i < inputAmounts.length; i++) {
			inputAmounts[i] = getStock(goods[i]).getQuantity();
		}
		return inputAmounts;
	}
	
}
