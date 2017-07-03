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
import java.util.Collection;
import java.util.StringTokenizer;

import com.agentecon.data.JsonData;
import com.agentecon.metric.Rank;
import com.agentecon.runner.SimulationStepper;

public class RankingMethod extends SimSpecificMethod {

	public RankingMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	public JsonData doExecute(StringTokenizer path, Parameters params) throws IOException {
		SimulationStepper simulation = getSimulation(path);
		return new Ranking(simulation.getRanking());
	}
	
	class Ranking extends JsonData {
		
		Collection<Rank> list;
		
		public Ranking(Collection<Rank> children) {
			this.list = children;
		}

	}

}
