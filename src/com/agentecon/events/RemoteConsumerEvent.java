/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.events;

import com.agentecon.AgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.LogUtil;

public class RemoteConsumerEvent extends ConsumerEvent {
	
	private int current;
	private AgentFactory[] sources;

	public RemoteConsumerEvent(int card, Endowment end, LogUtil util, AgentFactory... agentSources) {
		super(card, end, util);
		this.sources = agentSources;
		this.current = 0;
	}
	
	@Override
	protected IConsumer createConsumer(Endowment end, IUtility util){
		return new Consumer(end, util);
	}

}
