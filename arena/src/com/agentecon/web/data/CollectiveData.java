/**
 * Created by Luzius Meisser on Jun 14, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.data;

import java.util.Collection;
import java.util.Iterator;

import com.agentecon.agent.IAgent;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;

public class CollectiveData extends JsonData {

	public SourceData source;

	public int agents;
	public String[][] inventory;

	public CollectiveData(Collection<? extends IAgent> agents) {
		this.agents = agents.size();
		this.source = findCommonType(agents);
		Inventory base = new Inventory(new Good("temp"));
		for (IAgent a : agents) {
			base.absorb(a.getInventory().duplicate());
		}
		this.inventory = AgentData.toStringArray(base.getAll());
	}

	private SourceData findCommonType(Collection<? extends IAgent> agents) {
		if (agents.isEmpty()) {
			return null;
		} else {
			Iterator<? extends IAgent> iter = agents.iterator();
			String commonType = iter.next().getType();
			while (iter.hasNext()){
				if (!iter.next().getType().equals(commonType)){
					return null;
				}
			}
			return new SourceData(agents.iterator().next());
		}
	}

}
