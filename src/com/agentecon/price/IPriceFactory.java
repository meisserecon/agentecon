// Created on Jun 3, 2015 by Luzius Meisser

package com.agentecon.price;

import com.agentecon.good.Good;

public interface IPriceFactory {
	
	public IPrice createPrice(Good good);

}
