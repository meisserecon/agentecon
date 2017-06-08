// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.firm.sensor;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.OutputFactor;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;
import com.agentecon.price.AdaptablePrice;
import com.agentecon.price.IPrice;

public class SensorOutputFactor extends OutputFactor {
	
	private Ask prevRealAsk;
	private SensorAccuracy accuracy;

	public SensorOutputFactor(IStock stock, IPrice price) {
		this(stock, price, new SensorAccuracy());
	}

	public SensorOutputFactor(IStock stock, IPrice price, double accuracy) {
		this(stock, price, new SensorAccuracy(accuracy));
	}
	
	public SensorOutputFactor(IStock stock, IPrice price, SensorAccuracy accuracy) {
		super(stock, price);
		this.accuracy = accuracy;
	}
	
	@Override
	protected double getCurrentSuccessRate() {
		double sensorSize = accuracy.getOfferSize();
		double bulkSuccess = prevRealAsk.isUsed() ? 1.0 : 0.0;
		return sensorSize * super.getCurrentSuccessRate() + (1-sensorSize)*bulkSuccess;
	}

	@Override
	public double getVolume() {
		return super.getVolume() + (prevRealAsk == null ? 0.0 : prevRealAsk.getTransactionVolume());
	}

	@Override
	public void createOffers(IPriceMakerMarket market, IAgent owner, IStock money, double amount) {
		double sensorSize = accuracy.getOfferSize() * amount;
		super.createOffers(market, owner, money, sensorSize);
		prevRealAsk = new Ask(owner, money, getStock(), new Price(getGood(), getSafePrice()), amount - sensorSize);
		market.offer(prevRealAsk);
	}

	@Override
	public void adaptPrice() {
		super.adaptPrice();
		if (prevRealAsk != null) {
			if (prevRealAsk.isUsed()) {
				accuracy.moreAccurate();
			} else {
				accuracy.lessAccurate();
			}
		}
	}

	private double getSafePrice() {
		return Math.max(AdaptablePrice.MIN, super.getPrice() / (1 + accuracy.getAccuracy()));
	}

	@Override
	public double getPrice() {
		double offerSize = accuracy.getOfferSize();
		double sensor = super.getPrice();
		double most = getSafePrice();
		return offerSize * sensor + (1 - offerSize) * most;
	}

	@Override
	public OutputFactor duplicate(IStock stock) {
		return new SensorOutputFactor(stock, price, accuracy);
	}

}
