/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.graph;

import com.agentecon.agent.IAgent;
import com.agentecon.agent.IAgents;
import com.agentecon.web.query.AgentQuery;
import com.agentecon.web.query.ENodeType;

public class Node implements Comparable<Node>{

	public String label;
	public String parent;
	public int children;
	public double size;
	
	private transient AgentQuery query;
	private transient AgentSize sizeQuery;
	
	public Node(String agent) {
		this(agent, null);
	}
	
	public Node(String agent, ESizeType sizeType) {
		this.label = agent;
		this.query = new AgentQuery(agent);
		this.sizeQuery = 
	}
	
	public ENodeType getType(IAgents agents) {
		return query.getType(agents);
	}

	public boolean contains(IAgent agent) {
		return query.matches(agent);
	}

	public void fetchData(IAgents agents) {
		this.parent = query.getParent(agents);
		this.children = query.getChildren(agents).size();
		this.size = sizeQuery.complete(agents);
	}

	@Override
	public int compareTo(Node o) {
		return query.compareTo(o.query);
	}
	
	@Override
	public String toString(){
		return label;
	}
	
}
