package com.agentecon.market;

import java.util.Collection;

import com.agentecon.goods.Good;

public interface IMarketStatistics {
	
	public Collection<Good> getTradedGoods();
	
	public GoodStats getStats(Good good);
	
}
