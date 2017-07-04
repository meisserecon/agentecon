/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import com.agentecon.ISimulation;
import com.agentecon.data.JsonData;
import com.agentecon.data.TradeGraph;

public class TradeGraphMethod extends SimSpecificMethod {
	
	public TradeGraphMethod(ListMethod listing) {
		super(listing);
	}
	
	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "&agents=consumers,firms&step=1";
	}

	@Override
	public JsonData doExecute(StringTokenizer path, Parameters params) throws IOException {
		int day = params.getDay();
		int stepSize = params.getIntParam("step");
		ISimulation simulation = getSimulation(path, day - stepSize);
		List<String> agents = params.getFromCommaSeparatedList("agents");
		TradeGraph graph = new TradeGraph(simulation, agents);
		simulation.forwardTo(day);
		return graph.fetchData();
	}

}
