/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise2;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.agent.Endowment;
import com.agentecon.configuration.FarmingConfiguration;
import com.agentecon.configuration.HermitConfiguration;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others.
 * To formalize these relationships, the farmer does not produce himself anymore, but instead
 * uses his land to found a profit-maximizing firm.
 */
public class Farmer extends Consumer implements IFounder {
	
	private Good manhours;

	public Farmer(Endowment end, IUtility utility) {
		super(end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}
	
	@Override
	public IFirm considerCreatingFirm(IInnovation research) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.getAmount() >= 100.0){
			// I have plenty of land, lets create a new farm with me as owner
			IShareholder owner = Farmer.this;
			IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);
			
			IStock wallet = getMoney();
			IStock firmMoney = wallet.hideRelative(0.5);
			
			AdaptiveFarm farm = new AdaptiveFarm(owner, firmMoney, myLand, prod);
			farm.getInventory().getStock(manhours).transfer(getStock(manhours), 10);
			return farm;
		} else {
			return null;
		}
	}
	
	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		super.tradeGoods(market);
	}
	
	@Override
	public double consume() {
		return super.consume();
	}
	
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		FarmingConfiguration.main(args);
	}

}
