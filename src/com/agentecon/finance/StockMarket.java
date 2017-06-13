package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.Consumer;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.world.Agents;
import com.agentecon.world.World;

public class StockMarket extends SimulationListenerAdapter {

	private World world;
	private SimulationListeners listeners;

	public StockMarket(World world, SimulationListeners listeners) {
		this.listeners = listeners;
		this.world = world;
	}

	public void trade(int day) {
		Agents ags = world.getAgents();
		for (IFirm firm : ags.getPublicCompanies()) {
			firm.payDividends(day);
		}
		Collection<MarketMaker> mms = ags.getRandomizedMarketMakers();
		if (mms.isEmpty()) {
			// Assume model without stock market, distribute dividends proportionally among consumers
			distributeDividendsEqually(day, ags);
		} else {
			runDailyMarket(day, ags, mms);
		}
	}

	private void distributeDividendsEqually(int day, Agents ags) {
		IStock wallet = new Stock(Good.MONEY);
		for (IFirm firm : ags.getPublicCompanies()) {
			((ShareRegister)firm.getShareRegister()).collectRootDividend(wallet);
		}
		Collection<Consumer> consumers = ags.getAllConsumers();
		double dividend = wallet.getAmount() / consumers.size();
		for (Consumer cons: consumers){
			cons.getMoney().transfer(wallet, dividend);
		}
		if (!wallet.isEmpty()){
			consumers.iterator().next().getMoney().absorb(wallet);
		}
	}

	protected void runDailyMarket(int day, Agents ags, Collection<MarketMaker> mms) {
		for (IShareholder shareholder : ags.getShareholders()) {
			shareholder.getPortfolio().collectDividends();
		}
		DailyStockMarket dsm = new DailyStockMarket(world.getRand());
		listeners.notifyStockMarketOpened(dsm);
		
		for (MarketMaker mm : mms) {
			// System.out.println(day + ": " + mm);
			mm.postOffers(dsm);
		}
		// System.out.println(day + " trading stats " + dsm.getTradingStats());
		for (IFirm pc : ags.getPublicCompanies()) {
			pc.raiseCapital(dsm);
		}
		for (IShareholder con : ags.getRandomShareholders()) {
			con.managePortfolio(dsm);
		}
		dsm.close(day);
	}

	@Override
	public void notifyAgentCreated(IAgent firm) {
		if (firm instanceof IFirm) {
			notifyMarketMakers((IFirm) firm);
		}
	}

	private void notifyMarketMakers(IFirm comp) {
		ShareRegister register = (ShareRegister) comp.getShareRegister();
		Collection<MarketMaker> mms = world.getAgents().getAllMarketMakers();
		for (MarketMaker mm : mms) {
			mm.addPosition(register.createPosition());
		}
	}

}
