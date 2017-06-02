package com.agentecon.ranking;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.goods.Inventory;
import com.agentecon.sim.AgentRef;
import com.agentecon.sim.IAgent;
import com.agentecon.sim.SimulationListenerAdapter;
import com.agentecon.util.Average;

public class ConsumerRanking extends SimulationListenerAdapter {

	private ArrayList<ConsumerListener> list;
	
	public ConsumerRanking(){
		this.list = new ArrayList<>();
	}
	
	@Override
	public void notifyAgentCreated(IAgent agent) {
		if (agent instanceof IConsumer){
			ConsumerListener listener = new ConsumerListener(agent);
			list.add(listener);
			((IConsumer)agent).addListener(listener);
		}
	}
	
	public void print(PrintStream out) {
		Collections.sort(list);
		int rank = 1;
		System.out.println("Rank\tType\tId\tAvg Utility");
		for (ConsumerListener l: list){
			out.println(rank++ + "\t" + l);
		}
	}
	
	class ConsumerListener implements IConsumerListener, Comparable<ConsumerListener>{
		
		private AgentRef agent;
		private Average averageUtility = new Average();
		
		public ConsumerListener(IAgent agent){
			this.agent = agent.getReference();
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
			return averageUtility.compareTo(o.averageUtility);
		}
		
		public String toString(){
			IAgent agent = this.agent.get();
			return agent.getType() + "\t" + agent.getAgentId() + "\t" + averageUtility.getAverage();
		}
		
	}

}
