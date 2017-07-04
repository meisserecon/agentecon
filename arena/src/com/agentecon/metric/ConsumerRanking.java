/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.goods.Inventory;
import com.agentecon.sim.AgentRef;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.Average;
import com.agentecon.util.InstantiatingHashMap;

public class ConsumerRanking extends SimulationListenerAdapter {
	
	private ArrayList<ConsumerListener> list;

	public ConsumerRanking() {
		this.list = new ArrayList<>();
	}

	@Override
	public void notifyConsumerCreated(IConsumer consumer) {
		ConsumerListener listener = new ConsumerListener(consumer);
		list.add(listener);
		consumer.addListener(listener);
	}

	public void print(PrintStream out) {
		Collections.sort(list);
		int rank = 1;
		System.out.println("Rank\tType\tId\tAvg Utility");
		for (ConsumerListener l : list) {
			out.println(rank++ + "\t" + l);
		}
	}
	
	public Collection<Rank> getRanking(){
		HashMap<String, Rank> ranking = new InstantiatingHashMap<String, Rank>() {

			@Override
			protected Rank create(String key) {
				return new Rank(key);
			}
		};
		for (ConsumerListener listener: list){
			ranking.get(listener.getType()).add(listener.getAverage().getTotal());
		}
		ArrayList<Rank> list = new ArrayList<>(ranking.values());
		Collections.sort(list);
		return list;
	}

	class ConsumerListener implements IConsumerListener, Comparable<ConsumerListener> {

		private AgentRef agent;
		private Average averageUtility;

		public ConsumerListener(IAgent agent) {
			this.agent = agent.getReference();
			this.averageUtility = new Average();
		}
		
		public String getType(){
			return agent.get().getType();
		}
		
		public Average getAverage(){
			return averageUtility;
		}

		@Override
		public void notifyConsuming(IConsumer inst, int age, Inventory inv, double utility) {
			averageUtility.add(utility);
		}

		@Override
		public void notifyRetiring(IConsumer inst, int age) {
		}

		@Override
		public void notifyInvested(IConsumer inst, double amount) {
		}

		@Override
		public void notifyDivested(IConsumer inst, double amount) {
		}

		@Override
		public int compareTo(ConsumerListener o) {
			return o.averageUtility.compareTo(averageUtility);
		}

		@Override
		public String toString() {
			IAgent agent = this.agent.get();
			return agent.getType() + "\t" + agent.getAgentId() + "\t" + averageUtility.getAverage();
		}

	}
	
}


