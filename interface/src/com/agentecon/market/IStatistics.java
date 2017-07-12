/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.market;

import java.util.Random;

public interface IStatistics {
	
	public int getDay();
	
	public Random getRandomNumberGenerator();
	
	public IMarketStatistics getGoodsMarketStats();
	
	public IMarketStatistics getStockMarketStats();

}
