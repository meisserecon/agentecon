/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise1;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * An autarkic consumer that produces its own food and does not
 * interact with others.
 */
public class Hermit extends Consumer implements IFounder {
	
	private Good manhours;
	private IProductionFunction prodFun;

	public Hermit(Endowment end, IUtility utility) {
		super(end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
	}
	
	@Override
	public IFirm considerCreatingFirm(IInnovation research) {
		if (this.prodFun == null){
			// instead of creating a firm, the hermit will use the production function himself
			this.prodFun = research.createProductionFunction(HermitConfiguration.POTATOE);
		}
		return null;
	}
	
	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// Hermit does not trade, produces instead for himself
		produce(getInventory());
	}

	private void produce(Inventory inventory) {
		IStock currentManhours = inventory.getStock(manhours);

		// Play here. Maybe you find a better fraction than 70%?
		// getUtilityFunction().getWeights() might help you finding out
		// how the consumer weighs the utility of potatoes and of leisure
		// time (man-hours) relative to each other.
//		double plannedLeisureTime = currentManhours.getAmount() * 0.6;
		
		double weight = prodFun.getWeight(manhours).weight;
		double fixedCost = prodFun.getFixedCost(manhours);
		double optimalWorkAmount = (currentManhours.getAmount() * weight + fixedCost)/(1+weight);
		double optimalLeisureTime = currentManhours.getAmount() - optimalWorkAmount;
		
		// The hide function creates allows to hide parts of the inventory from the
		// production function, preserving it for later consumption.
		Inventory productionInventory = inventory.hide(manhours, optimalLeisureTime);
		prodFun.produce(productionInventory);
	}
	
	@Override
	// this method is not needed and only here for better explanation what is going on
	public double consume() {
		// super class already knows how to consume, let it do the work
		return super.consume();
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		HermitConfiguration.main(args); // the HermitConfiguration knows what to do
	}

}
