/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.consumer.IConsumer;
import com.agentecon.firm.IFirm;
import com.agentecon.web.methods.Parameters;

public class AgentQuery {
	
	private EQueryType type;
	private String query;
	private int id;

	public AgentQuery(String query) {
		this.query = query;
		this.type = EQueryType.derive(query);
		if (this.type == EQueryType.ID) {
			this.id = Integer.parseInt(query);
		}
	}
	
	public static final String getExample(){
		return Parameters.SELECTION + "=" + EQueryType.FIRMS_QUERY;
	}
	
	public Collection<String> getChildren(IAgents agents) {
		switch (type) {
		case CONSUMERS:
			return agents.getConsumerTypes();
		case FIRMS:
			return agents.getFirmTypes();
		case TYPE:
			Collection<IAgent> agentsOfType = agents.getAgents(query);
			ArrayList<String> ids = new ArrayList<>(agentsOfType.size());
			for (IAgent agent : agentsOfType) {
				ids.add(Integer.toString(agent.getAgentId()));
			}
			return ids;
		default:
			return Collections.emptyList();
		}
	}

	public boolean matches(IAgent agent) {
		switch (type) {
		case CONSUMERS:
			return agent instanceof IConsumer;
		case FIRMS:
			return agent instanceof IFirm;
		case ID:
			return agent.getAgentId() == id;
		case TYPE:
			return agent.getType().equals(query);
		default:
			return false;
		}
	}

	public JsonData getAgentData(IAgents agents) {
		switch (type) {
		case CONSUMERS:
			return new CollectiveConsumerData(agents.getConsumers());
		case FIRMS:
			return new CollectiveFirmData(agents.getFirms());
		case ID:
			IAgent agent = agents.getAgent(id);
			if (agent instanceof IConsumer) {
				return new ConsumerData((IConsumer) agent);
			} else if (agent instanceof IFirm) {
				return new FirmData((IFirm) agent);
			} else {
				assert false : "Agent " + query + " not found";
				return null;
			}
		case TYPE:
			if (agents.getConsumerTypes().contains(query)) {
				return new CollectiveConsumerData(extract(agents.getConsumers(), query));
			} else {
				assert agents.getFirmTypes().contains(query);
				return new CollectiveFirmData(extract(agents.getFirms(), query));
			}
		default:
			assert false : query + " not found";
			return null;
		}
	}

	private <T extends IAgent> Collection<T> extract(Collection<T> agents, String selection) {
		ArrayList<T> list = new ArrayList<>();
		for (T agent : agents) {
			if (agent.getType().equals(selection)) {
				list.add(agent);
			}
		}
		return list;
	}

	public int compareTo(AgentQuery query) {
		return type.ordinal() - query.type.ordinal();
	}

}