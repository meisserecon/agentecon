package com.agentecon.events;

import com.agentecon.agent.Endowment;
import com.agentecon.firm.Firm;
import com.agentecon.firm.production.IProductionFunction;
import com.agentecon.firm.sensor.SensorFirm;
import com.agentecon.price.PriceFactory;
import com.agentecon.world.IWorld;

public class FirmEvent extends SimEvent {

	private String type;
	protected Endowment end;
	protected IProductionFunction prodFun;
	protected String[] priceParams;
	private boolean sensor;

	public FirmEvent(int card, String type, Endowment end, IProductionFunction prodFun, boolean sensor, String... priceParams) {
		this(0, card, type, end, prodFun, sensor, priceParams);
	}

	public FirmEvent(int step, int card, String type, Endowment end, IProductionFunction prodFun, boolean sensor, String... priceParams) {
		super(step, card);
		this.end = end;
		this.type = type;
		this.prodFun = prodFun;
		this.sensor = sensor;
		this.priceParams = priceParams;
	}

	@Override
	public void execute(IWorld sim) {
		PriceFactory pf = new PriceFactory(sim.getRand(), priceParams);
		for (int i = 0; i < getCardinality(); i++) {
			sim.getFirms().add(createFirm(type, end, prodFun, pf));
		}
	}

	protected Firm createFirm(String type, Endowment end, IProductionFunction prodFun, PriceFactory pf) {
		if (sensor) {
			return new SensorFirm(type, end, prodFun, pf);
		} else {
			return new Firm(type, end, prodFun, pf);
		}
	}

}
