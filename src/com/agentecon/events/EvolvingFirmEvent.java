package com.agentecon.events;

import java.util.ArrayList;
import java.util.Random;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Firm;
import com.agentecon.firm.LogProdFun;
import com.agentecon.firm.SensorFirm;
import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;
import com.agentecon.price.RotatingPriceFactory;
import com.agentecon.util.Average;
import com.agentecon.world.IWorld;

public class EvolvingFirmEvent extends EvolvingEvent {

	private Endowment end;
	private FirstDayProduction prod;
	private LogProdFun prodFun;
	private ArrayList<Firm> firms;
	private RotatingPriceFactory rotPrices;

	public EvolvingFirmEvent(int firmsPerType, String type, Endowment end, LogProdFun fun, Random rand, String... priceParams) {
		super(0, firmsPerType);
		this.end = end;
		this.prodFun = fun;
		this.firms = new ArrayList<>();
		this.rotPrices = new RotatingPriceFactory(rand);
		for (int i = 0; i < getCardinality(); i++) {
			firms.add(new SensorFirm(type, end, fun, rotPrices));
//			firms.add(new SensorFirm(type, end, fun, new PriceFactory(rand, priceParams[0], priceParams[1])));
		}
		initListener();
	}

	private void initListener() {
		this.prod = new FirstDayProduction(firms.size());
		for (Firm firm : firms) {
			firm.addFirmMonitor(prod);
		}
	}

	private EvolvingFirmEvent(int cardinality, Endowment end, LogProdFun prodFun, ArrayList<Firm> firms) {
		super(0, cardinality);
		this.end = end;
		this.prodFun = prodFun;
		this.firms = firms;
		initListener();

	}

	@Override
	public EvolvingEvent createNextGeneration() {
		ArrayList<Firm> newFirms = new ArrayList<>();
		adaptEndowment();
		for (Firm firm : firms) {
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
		for (Firm firm : firms) {
			avg.add(firm.getOutputPrice());
		}
		return avg.getAverage();
	}

	@Override
	public void execute(IWorld sim) {
		sim.addListener(rotPrices);
		for (Firm firm : firms) {
			sim.getFirms().add(firm);
		}
	}

	public String toString() {
		return "Firms with average price " + getScore();
	}

}
