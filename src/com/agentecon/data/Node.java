/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.data;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;

public class Node implements Comparable<Node>{

	public String label;
	public int children;
	
	private transient AgentQuery query;
	
	public Node(String agent) {
		this.label = agent;
		this.query = new AgentQuery(agent);
	}

	public boolean contains(IAgent agent) {
		return query.matches(agent);
	}

	public void fetchData(IAgents agents) {
		this.children = query.getChildren(agents).size();
	}

	@Override
	public int compareTo(Node o) {
		return query.compareTo(o.query);
	}

}
