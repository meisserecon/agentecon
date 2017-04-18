package com.agentecon.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.agentecon.api.IConsumer;
import com.agentecon.api.ISimulation;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.util.InstantiatingHashMap;

public class Demographics extends SimStats {

	private ISimulation world;
	private TimeSeries retired;
	private TimeSeries working;
	private TimeSeries population, dependency, dailyutility;
	private HashMap<String, AveragingTimeSeries> utilityOnDeath;

	public Demographics(ISimulation world) {
		this.world = world;
		this.retired = new TimeSeries("Retirees");
		this.working = new TimeSeries("Workers");
		this.population = new TimeSeries("Population");
		this.dependency = new TimeSeries("Dependency Ratio");
		this.dailyutility = new TimeSeries("Utility today");
		this.utilityOnDeath = new InstantiatingHashMap<String, AveragingTimeSeries>() {

			@Override
			protected AveragingTimeSeries create(String key) {
				return new AveragingTimeSeries(key);
			}
		};
	}

	@Override
	public void notifyDayEnded(int day, double utility) {
		dailyutility.set(day, utility);
		for (AveragingTimeSeries pc : utilityOnDeath.values()) {
			pc.push(day);
		}
		Collection<? extends IConsumer> cons = world.getConsumers();
		int retired = 0, working = 0, total = 0;
		for (IConsumer c : cons) {
			total++;
			if (c.isRetired()) {
				retired++;
			} else {
				working++;
			}
		}
		this.retired.set(day, retired);
		this.population.set(day, total);
		this.working.set(day, working);
		if (retired > 0){
			this.dependency.set(day, ((double) working) / retired);
		}
	}

	@Override
	public void notfiyConsumerDied(IConsumer consumer) {
		utilityOnDeath.get(consumer.getType()).add(consumer.getTotalExperiencedUtility());
	}

	public Collection<? extends TimeSeries> getUtilityData() {
		ArrayList<TimeSeries> ts = new ArrayList<>();
		for (AveragingTimeSeries pc : utilityOnDeath.values()) {
			ts.add(pc.getTimeSeries());
		}
		return ts;
	}

	public boolean isRelevat() {
		return false;
	}

	@Override
	public Collection<? extends Chart> getCharts(String simId) {
		return Arrays.asList(new Chart(simId, "Population", "Retired, working and total population", retired, working, population),
				new Chart(simId, "Dependency Ratio", "Retirees per workers", dependency), new Chart(simId, "Utility", "Accumulated life-time utility on day of death", getUtilityData()));
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> all = new ArrayList<>();
		all.addAll(Arrays.asList(retired, working, population, dependency));
		all.addAll(TimeSeries.prefix("Utility on death", getUtilityData()));
		all.add(dailyutility);
		return all;
	}

}
