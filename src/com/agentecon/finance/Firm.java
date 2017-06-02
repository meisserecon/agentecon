package com.agentecon.finance;

import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.firm.FirmListeners;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;

public abstract class Firm extends Agent implements IFirm {

	private Ticker ticker;
	private ShareRegister register;
	private FirmListeners monitor;
	
	public Firm(String type, Endowment end) {
		super(type, end);
		this.ticker = new Ticker(type, getAgentId());
		this.register = new ShareRegister(ticker, getDividendWallet());
		this.monitor = new FirmListeners();
	}
	
	@Override
	public ShareRegister getShareRegister() {
		return register;
	}
	
	@Override
	public Ticker getTicker() {
		return ticker;
	}
	
	public void addFirmMonitor(IFirmListener prodmon) {
		this.monitor.add(prodmon);
	}
	
	@Override
	public void inherit(Position pos) {
		register.inherit(pos);
	}

	@Override
	public void raiseCapital(Object stockmarket) {
		register.raiseCapital((DailyStockMarket) stockmarket, getDividendWallet());
	}

	protected abstract double calculateDividends(int day);
	
	@Override
	public void payDividends(int day) {
		double dividend = calculateDividends(day);
		if (dividend > 0){
			monitor.reportDividend(this, dividend);
			register.payDividend(getDividendWallet(), dividend);
		}
	}

	protected IStock getDividendWallet() {
		return getMoney();
	}

}
