package com.agentecon.finance;

import java.util.Collection;

import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IShareholder;
import com.agentecon.sim.SimulationListeners;
import com.agentecon.world.Agents;
import com.agentecon.world.Country;

public class StockMarket {

	private Country country;
	private SimulationListeners listeners;

	public StockMarket(Country world, SimulationListeners listeners) {
		this.listeners = listeners;
		this.country = world;
	}

	public void trade(int day) {
		Agents ags = country.getAgents();
		for (IFirm firm : ags.getFirms()) {
			firm.payDividends(day);
		}
		Collection<IMarketMaker> mms = ags.getRandomizedMarketMakers();
		runDailyMarket(day, ags, mms);
	}

	protected void runDailyMarket(int day, Agents ags, Collection<IMarketMaker> mms) {
		for (IShareholder shareholder : ags.getShareholders()) {
			shareholder.getPortfolio().collectDividends();
		}
		DailyStockMarket dsm = new DailyStockMarket(country.getRand());
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
