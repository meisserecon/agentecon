/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise3;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentId;
import com.agentecon.finance.Firm;
import com.agentecon.finance.MarketMakerPrice;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.FinanceDepartment;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.IGoodsTrader;

public class RealEstateAgent extends Firm implements IGoodsTrader {
	
	private Good land;
	private FinanceDepartment finance;
	private MarketMakerPrice priceBelief;

	public RealEstateAgent(IAgentId id, IShareholder owner, IStock initialMoney, IStock initialLand) {
		super(id, owner, new Endowment(initialMoney.getGood()));
		getMoney().absorb(initialMoney);
		
		this.land = initialLand.getGood();
		IStock ownedLand = getStock(this.land);
		ownedLand.absorb(initialLand);
		this.priceBelief = new MarketMakerPrice(ownedLand);
		this.finance = new FinanceDepartment(null);
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		this.priceBelief.trade(market, this, getMoney(), getMoney().getAmount() / 10);
	}
	
	@Override
	protected double calculateDividends(int day) {
		double profits = 0.0;
		double size = getMoney().getAmount();
		return finance.calculateDividends();
	}

}
