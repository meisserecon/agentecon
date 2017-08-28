/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.Firm;
import com.agentecon.finance.MarketMakerPrice;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.IGoodsTrader;

public class RealEstateAgent extends Firm implements IGoodsTrader {
	
	private static final double DISTRIBUTION_RATIO = 0.02;
	
	private Good land;
	private double minCashLevel;
	private MarketMakerPrice priceBelief;

	public RealEstateAgent(IAgentIdGenerator id, IShareholder owner, IStock initialMoney, IStock initialLand) {
		super(id, owner, new Endowment(initialMoney.getGood()));
		getMoney().absorb(initialMoney);
		
		this.land = initialLand.getGood();
		this.minCashLevel = initialMoney.getAmount();
		IStock ownedLand = getStock(this.land);
		ownedLand.absorb(initialLand);
		this.priceBelief = new MarketMakerPrice(ownedLand);
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		this.priceBelief.trade(market, this, getMoney(), getMoney().getAmount() / 10);
	}
	
	@Override
	public void adaptPrices() {
		// done during offer phase
	}
	
	@Override
	protected double calculateDividends(int day) {
		return getMoney().getAmount() * DISTRIBUTION_RATIO - minCashLevel;
	}

}
