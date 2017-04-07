package com.agentecon.events;

import java.util.ArrayList;
import java.util.Collection;

import com.agentecon.agent.AgentRef;
import com.agentecon.agent.Endowment;
import com.agentecon.consumer.ReincarnatingConsumer;
import com.agentecon.good.Good;
import com.agentecon.util.Average;
import com.agentecon.world.IWorld;

public class SavingConsumerEvent extends EvolvingEvent {

	private Endowment end;
	private IUtilityFactory utilfac;
	private ArrayList<AgentRef> consumers;

	public SavingConsumerEvent(int card, String name, Endowment end, IUtilityFactory utilfac, Good goodToSave, double savingsRate) {
		super(0, card);
		this.end = end;
		this.utilfac = utilfac;
		this.consumers = new ArrayList<>();
		for (int i = 0; i < card; i++) {
			consumers.add(new ReincarnatingConsumer(name, end, utilfac.create(i), goodToSave, savingsRate).getRef());
		}
	}

	public SavingConsumerEvent(ArrayList<AgentRef> next, Endowment end, IUtilityFactory utilfac) {
		super(0, next.size());
		this.end = end;
		this.utilfac = utilfac;
		this.consumers = next;
	}

	@Override
	public void execute(IWorld sim) {
		for (AgentRef sc : consumers) {
			sim.add(sc.get());
		}
	}

	public EvolvingEvent createNextGeneration() {
		System.out.println(this.toString());
		ArrayList<AgentRef> next = new ArrayList<>();
		int i = 0;
		for (AgentRef ref : consumers) {
			ReincarnatingConsumer sc = (ReincarnatingConsumer) ref.get();
			next.add(sc.getNextGeneration(utilfac.create(i++), end).getRef());
		}
		return new SavingConsumerEvent(next, end, utilfac);
	}

	public double getDailySavings() {
		Average avg = new Average();
		for (ReincarnatingConsumer sc : getSavers()) {
			avg.add(1.0, sc.getDailySavings());
		}
		return avg.getAverage();
	}

	public double getDailyConsumption() {
		Average avg = new Average();
		for (ReincarnatingConsumer sc : getSavers()) {
			avg.add(1.0, sc.getAverageConsumption());
		}
		return avg.getAverage();
	}
	
	public double getPhaseOne() {
		Average avg = new Average();
		for (ReincarnatingConsumer sc : getSavers()) {
			avg.add(1.0, sc.getPhaseOneConsumption());
		}
		return avg.getAverage();
	}
	
	public double getPhaseTwo() {
		Average avg = new Average();
		for (ReincarnatingConsumer sc : getSavers()) {
			avg.add(1.0, sc.getPhaseTwoConsumption());
		}
		return avg.getAverage();
	}

	private double getDailyLeisure() {
		Average avg = new Average();
		for (ReincarnatingConsumer sc : getSavers()) {
			avg.add(1.0, sc.getAverageLeisure());
		}
		return avg.getAverage();
	}

	private Collection<ReincarnatingConsumer> getSavers() {
		ArrayList<ReincarnatingConsumer> list = new ArrayList<>();
		for (AgentRef ref : consumers) {
			list.add((ReincarnatingConsumer) ref.get());
		}
		return list;
	}

	@Override
	public double getScore() {
		Average avg = new Average();
		for (ReincarnatingConsumer sc : getSavers()) {
			avg.add(1.0, sc.getTotalExperiencedUtility());
		}
		return avg.getAverage();
	}

	@Override
	public String toString() {
		return "Consumers with daily savings of\t" + getDailySavings() + "\tand daily consumption of\t" + getDailyConsumption() + "\t" + getPhaseOne() + "\t" + getPhaseTwo() + "\toutput and " + getDailyLeisure() + " leisure";
	}

}
