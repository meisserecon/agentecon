package com.agentecon.events;

import java.util.Random;

import org.junit.Test;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.LogUtil;
import com.agentecon.goods.Good;
import com.agentecon.goods.Stock;
import com.agentecon.world.Agents;
import com.agentecon.world.IWorld;

public class SinConsumerEventTest implements IWorld {
	
	private int day;
	private int consumers;

	@Test
	public void test() {
		SinConsumerEvent e = new SinConsumerEvent(50, 7, 100, 500, 150, "test", new Endowment(new Stock(Good.MONEY, 1)), new LogUtil());
		for (day = 50; day<200; day++){
			e.execute(day, this);
		}
		assert consumers == 107;
	}

	@Override
	public Random getRand() {
		return null;
	}

	@Override
	public int getDay() {
		return day;
	}

	public void add(IAgent agent) {
		consumers++;
	}

	@Override
	public Agents getAgents() {
		return null;
	}

}
