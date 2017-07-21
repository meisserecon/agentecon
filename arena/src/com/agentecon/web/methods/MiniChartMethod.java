package com.agentecon.web.methods;

import java.io.IOException;

import com.agentecon.metric.minichart.MiniChart;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.query.AgentQuery;

public class MiniChartMethod extends SimSpecificMethod {
	
	private static final String KEY_PREFIX = "MiniChart-";
	private static final String HEIGHT_PARAMETER = "height";
	
	public MiniChartMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "&" + AgentQuery.getExample() + "&" + HEIGHT_PARAMETER + "=300";
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException {
		SimulationStepper stepper = getSimulation(params);
		MiniChart chart = (MiniChart) stepper.getCachedItem(KEY_PREFIX + params.getSelection());
		if (chart == null){
			chart = MiniChart.create(params.getSelection());
			stepper.putCached(KEY_PREFIX + params.getSelection(), chart);
		}
		return chart.getData(params.getDay(), stepper, params.getIntParam(HEIGHT_PARAMETER, 200));
	}
	
}
