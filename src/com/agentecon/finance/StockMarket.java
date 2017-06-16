package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.world.Agents;
import com.agentecon.world.World;

public class StockMarket {

	private World world;
	private SimulationListeners listeners;

	public StockMarket(World world, SimulationListeners listeners) {
		this.listeners = listeners;
		this.world = world;
	}

	public void trade(int day) {
		Agents ags = world.getAgents();
		for (IFirm firm : ags.getFirms()) {
			firm.payDividends(day);
		}
		Collection<IMarketMaker> mms = ags.getRandomizedMarketMakers();
		if (mms.isEmpty()) {
			// Assume model without stock market, distribute dividends proportionally among consumers
			distributeDividendsEqually(day, ags);
		} else {
			runDailyMarket(day, ags, mms);
		}
	}

	private void distributeDividendsEqually(int day, Agents ags) {
		IStock wallet = new Stock(Good.MONEY);
		for (IFirm firm : ags.getFirms()) {
			((ShareRegister)firm.getShareRegister()).collectRootDividend(wallet);
		}
		Collection<IConsumer> consumers = ags.getConsumers();
		double dividend = wallet.getAmount() / consumers.size();
		for (IConsumer cons: consumers){
			cons.getMoney().transfer(wallet, dividend);
		}
		if (!wallet.isEmpty()){
			consumers.iterator().next().getMoney().absorb(wallet);
		}
	}

	protected void runDailyMarket(int day, Agents ags, Collection<IMarketMaker> mms) {
		for (IShareholder shareholder : ags.getShareholders()) {
			shareholder.getPortfolio().collectDividends();
		}
		DailyStockMarket dsm = new DailyStockMarket(world.getRand());
		listeners.notifyStockMarketOpened(dsm);
		
		for (IMarketMaker mm : mms) {
			// System.out.println(day + ": " + mm);
			mm.postOffers(dsm);
		}
		// System.out.println(day + " trading stats " + dsm.getTradingStats());
		for (IFirm pc : ags.getFirms()) {
			pc.raiseCapital(dsm);
		}
		for (IShareholder con : ags.getRandomShareholders()) {
			con.managePortfolio(dsm);
		}
		dsm.close(day);
	}

}
