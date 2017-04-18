package com.agentecon.finance;

import com.agentecon.good.IStock;

public interface ITax {

	public void collect(IStock wallet, double latestDividends);

}
