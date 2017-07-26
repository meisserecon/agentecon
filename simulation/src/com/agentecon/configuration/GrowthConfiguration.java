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
import com.agentecon.events.ConsumerEvent;
import com.agentecon.goods.Stock;

public class GrowthConfiguration extends FarmingConfiguration {

	public GrowthConfiguration() throws IOException{
		Endowment consumerEndowment = new Endowment(GOLD, new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT));
		addEvent(new ConsumerEvent(1000, 1, 10, consumerEndowment, this));
	}

}
