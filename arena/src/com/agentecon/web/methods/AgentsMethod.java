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

import com.agentecon.ISimulation;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.query.AgentQuery;

public class AgentsMethod extends SimSpecificMethod {

	public AgentsMethod(ListMethod listing) {
		super(listing);
	}
	
	@Override
	protected String createExamplePath() {
		return super.createExamplePath() + "&" + AgentQuery.getExample();
	}

	@Override
	public JsonData doExecute(Parameters params) throws IOException {
		ISimulation sim = super.getSimulation(params, params.getDay());
		AgentQuery query = new AgentQuery(params.getSelection());
		return query.getAgentData(sim.getAgents());
	}
	
}
