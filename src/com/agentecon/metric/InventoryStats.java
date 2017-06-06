package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IMarket;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.MinMaxTimeSeries;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.sim.Event;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;
import com.agentecon.util.Numbers;

public class InventoryStats extends SimStats {

	private double money;
	private boolean allowMoneySupplyChange;

	private ISimulation sim;
	private HashMap<Good, MinMaxTimeSeries> consumerInv;
	private HashMap<Good, MinMaxTimeSeries> firmInv;
	private HashMap<Good, MinMaxTimeSeries> traderInv;
	// private HashMap<IFirm, HashMap<Good, TimeSeries>> individualInventories;
	private TimeSeries moneySupply;

	public InventoryStats(ISimulation sim) {
		this.sim = sim;
		this.money = 0.0;
		this.moneySupply = new TimeSeries("Money Supply");
		this.allowMoneySupplyChange = true;
		this.firmInv = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {

			@Override
			protected MinMaxTimeSeries create(Good key) {
				return new MinMaxTimeSeries(key.getName() + " inventory");
			}
		};
		this.consumerInv = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {

			@Override
			protected MinMaxTimeSeries create(Good key) {
				return new MinMaxTimeSeries(key.getName() + " inventory");
			}
		};
		this.traderInv = new InstantiatingHashMap<Good, MinMaxTimeSeries>() {

			@Override
			protected MinMaxTimeSeries create(Good key) {
				return new MinMaxTimeSeries(key.getName() + " inventory");
			}
		};
		// this.individualInventories = new InstantiatingHashMap<IFirm, HashMap<Good, TimeSeries>>() {
		//
		// @Override
		// protected HashMap<Good, TimeSeries> create(IFirm key) {
		// return new InstantiatingHashMap<Good, TimeSeries>() {
		//
		// @Override
		// protected TimeSeries create(Good key) {
		// return new TimeSeries(key.getName());
		// }
		//
		// };
		// }
		// };
	}

	@Override
	public void notifyEvent(Event e) {
		// event might change money supply
		allowMoneySupplyChange = true;
	}

	@Override
	public void notifyMarketClosed(IMarket market, boolean fin) {
		if (fin) {
			HashMap<Good, Average> all = new InstantiatingHashMap<Good, Average>() {

				@Override
				protected Average create(Good key) {
					return new Average();
				}

			};
			for (IAgent ag : sim.getFirms()) {
				Inventory inv = ag.getInventory();
				for (IStock stock : inv.getAll()) {
					if (!stock.isEmpty()) {
						// individualInventories.get(ag).get(stock.getGood()).set(sim.getDay(), stock.getAmount());
						all.get(stock.getGood()).add(1.0, stock.getAmount());
					}
				}
			}
			for (Map.Entry<Good, Average> entry : all.entrySet()) {
				firmInv.get(entry.getKey()).set(sim.getDay(), entry.getValue());
			}
		}
		if (fin) {
			HashMap<Good, Average> all = new InstantiatingHashMap<Good, Average>() {

				@Override
				protected Average create(Good key) {
					return new Average();
				}

			};
			for (IAgent ag : sim.getConsumers()) {
				Inventory inv = ag.getInventory();
				for (IStock stock : inv.getAll()) {
					if (!stock.isEmpty() && stock.getGood().getPersistence() > 0.0) {
						all.get(stock.getGood()).add(1.0, stock.getAmount());
					}
				}
			}
			for (Map.Entry<Good, Average> entry : all.entrySet()) {
				consumerInv.get(entry.getKey()).set(sim.getDay(), entry.getValue());
			}
		}

		double money = 0.0;
		for (IAgent a : sim.getAgents()) {
			money += a.getMoney().getAmount();
		}
		if (this.money != money && allowMoneySupplyChange) {
			this.allowMoneySupplyChange = false;
			this.money = money;
		} else {
			assert Numbers.equals(this.money, money);
		}
		if (fin) {
			moneySupply.set(sim.getDay(), money);
		}

	}

	public Collection<Chart> getCharts(String parentId) {
		ArrayList<Chart> charts = new ArrayList<>();
		charts.add(new Chart(parentId, "Firm Inventory", "Average firm inventory after trading, but before production and consumption", firmInv.values()));
		charts.add(new Chart(parentId, "Consumer Inventory", "Average consumer inventory after trading, but before production and consumption", consumerInv.values()));
		if (!traderInv.isEmpty()) {
			charts.add(new Chart(parentId, "Trader Inventory", "Average trader inventory after trading, but before production and consumption", traderInv.values()));
		}
		if (moneySupply.isInteresting()) {
			charts.add(new Chart(parentId, "Money Supply", "Total money supply in the economy", Collections.singleton(moneySupply)));
		}
		// for (Map.Entry<IFirm, HashMap<Good, TimeSeries>> e : individualInventories.entrySet()) {
		// charts.add(new Chart(parentId, "Inventory of " + e.getKey().getName(), "Inventory of " + e.getKey().getName() + " after trading, before production", e.getValue().values()));
		// }
		return charts;
	}
	
	@Override
	public ArrayList<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(TimeSeries.prefix("Inventory", firmInv.values()));
		list.addAll(TimeSeries.prefix("Inventory", consumerInv.values()));
		list.add(moneySupply);
		return list;
	}

}
