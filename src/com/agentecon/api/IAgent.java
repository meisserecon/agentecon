// Created on Jun 1, 2015 by Luzius Meisser

package com.agentecon.api;

import com.agentecon.good.IStock;
import com.agentecon.good.Inventory;

public interface IAgent extends Cloneable {
	
	public String getType();
	
	public String getName();
	
	public IStock getMoney();

	public Inventory getInventory();
	
	public void collectDailyEndowment();
	
	public boolean isAlive();
	
	public IAgent clone();
	
}
