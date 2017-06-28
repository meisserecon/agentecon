/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercise1.Hermit;
import com.agentecon.exercise2.Farmer;

public class AgentFactory implements IAgentFactory {

	@Override
	public IConsumer createConsumer(EConsumerType type, Endowment endowment, IUtility utilityFunction) {
		switch (type) {
		case HERMIT:
			return new Hermit(endowment, utilityFunction);
		case FARMER:
			return new Farmer(endowment, utilityFunction);
		default:
			return null;
		}
	}

}
