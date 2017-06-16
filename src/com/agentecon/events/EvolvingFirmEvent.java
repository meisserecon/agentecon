package com.agentecon.events;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Producer;
import com.agentecon.firm.production.LogProdFun;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.util.Average;
import com.agentecon.world.IWorld;

public class EvolvingFirmEvent extends EvolvingEvent {

	private Endowment end;
	private FirstDayProduction prod;
	private LogProdFun prodFun;
	private ArrayList<Producer> firms;

	public EvolvingFirmEvent(int firmsPerType, String type, Endowment end, LogProdFun fun, Random rand) {
		super(0, firmsPerType);
		this.end = end;
		this.prodFun = fun;
		this.firms = new ArrayList<>();
		for (int i = 0; i < getCardinality(); i++) {
			firms.add(new Producer(end, fun));
		}
		initListener();
	}

	private void initListener() {
		this.prod = new FirstDayProduction(firms.size());
		for (Producer firm : firms) {
			firm.addProducerMonitor(prod);
		}
	}

	private EvolvingFirmEvent(int cardinality, Endowment end, LogProdFun prodFun, ArrayList<Producer> firms) {
		super(0, cardinality);
		this.end = end;
		this.prodFun = prodFun;
		this.firms = firms;
		initListener();

	}

	@Override
	public EvolvingEvent createNextGeneration() {
		ArrayList<Producer> newFirms = new ArrayList<>();
		adaptEndowment();
		for (Producer firm : firms) {
			newFirms.add(firm.createNextGeneration(end, prodFun));
		}
		return new EvolvingFirmEvent(getCardinality(), end, prodFun, newFirms);
	}

	private void adaptEndowment() {
		Inventory inv = end.getInitialInventory();
		IStock stock = inv.getStock(prod.getGood());
		double diff = prod.getAmount() - stock.getAmount();
		if (diff > 0) {
			stock.add(diff);
		} else {
			stock.remove(-diff);
		}
		end = new Endowment(inv.getAll().toArray(new IStock[]{}), end.getDaily());
	}

	@Override
	public double getScore() {
		Average avg = new Average();
		for (Producer firm : firms) {
			avg.add(firm.getOutputPrice());
		}
		return avg.getAverage();
	}

	@Override
	public void execute(int day, IWorld sim) {
		for (Producer firm : firms) {
			sim.add(firm);
		}
	}

	public String toString() {
		return "Firms with average price " + getScore();
	}

}
