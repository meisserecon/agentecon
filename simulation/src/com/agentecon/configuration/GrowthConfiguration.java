/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;

import com.agentecon.agent.Endowment;
import com.agentecon.consumer.Consumer;
import com.agentecon.events.GrowthEvent;
import com.agentecon.goods.Stock;
import com.agentecon.world.ICountry;

public class GrowthConfiguration extends FarmingConfiguration {

	public GrowthConfiguration() throws IOException{
		final Endowment consumerEndowment = new Endowment(GOLD, new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT));
		addEvent(new GrowthEvent(0, 0.001) {
			
			@Override
			protected void execute(ICountry sim) {
				sim.add(new Consumer(sim.getAgents(), consumerEndowment, create(0)));
			}
		});
	}

}
