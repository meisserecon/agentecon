package com.agentecon.finance;

import com.agentecon.SimAgent;
import com.agentecon.agent.Endowment;
import com.agentecon.firm.FirmListeners;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;

public abstract class Firm extends SimAgent implements IFirm {

	private Ticker ticker;
	private ShareRegister register;
	private FirmListeners monitor;

	public Firm(IShareholder owner, Endowment end) {
		this(end);
		Position ownerPosition = this.register.createPosition();
		this.register.claimCompanyShares(ownerPosition);
		owner.getPortfolio().addPosition(ownerPosition);
	}
	
	public Firm(Endowment end) {
		super(end);
		this.ticker = new Ticker(getType(), getAgentId());
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
	public void raiseCapital(IStockMarket stockmarket) {
		register.raiseCapital(stockmarket, this, getDividendWallet());
	}

	protected abstract double calculateDividends(int day);

	@Override
	public final void payDividends(int day) {
		double dividend = calculateDividends(day);
		if (dividend > 0) {
			monitor.reportDividend(this, dividend);
			register.payDividend(getDividendWallet(), dividend);
		}
	}

	protected IStock getDividendWallet() {
		return getMoney();
	}

}
