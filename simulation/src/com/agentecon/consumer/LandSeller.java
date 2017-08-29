/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.consumer;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.FarmingConfiguration;
import com.agentecon.configuration.HermitConfiguration;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;

/**
 * Sells land and invests the proceeds into stocks if possible.
 */
public class LandSeller extends Consumer {

	private static final double MINIMUM_WORKING_HOURS = Farmer.MINIMUM_WORKING_HOURS;

	private Good manhours;
	private double moneyToInvest;

	public LandSeller(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		IStock myLand = getStock(FarmingConfiguration.LAND);
		IStock wallet = getMoney();
		double money = wallet.getAmount();
		market.sellSome(this, wallet, myLand);
		moneyToInvest += wallet.getAmount() - money;
		
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), 0.8);
		super.trade(reducedInv, market);
	}
	
	@Override
	public void managePortfolio(IStockMarket dsm) {
		double invested = getPortfolio().invest(dsm, this, moneyToInvest);
		this.moneyToInvest -= invested;
	}

	@Override
	public double consume() {
		return super.consume();
	}

}
