package com.agentecon.agent;

import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.sim.AgentRef;
import com.agentecon.sim.IAgent;
import com.agentecon.sim.config.SimConfig;

public abstract class Agent implements IAgent, Cloneable {

	private static int current = 1;
	
	private int number;
	private String type;
	private Endowment end;
	private Inventory inv;
	private boolean alive;
	
	private AgentRef ref;

	public Agent(String type, Endowment end) {
		this.type = type;
		this.inv = end.getInitialInventory();
		this.end = end;
		this.number = current++;
		this.alive = true;
		this.ref = new AgentRef(this);
		assert type != null;
	}

	public AgentRef getReference(){
		return ref;
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public String getName() {
		return getType() + " " + number;
	}

	public final String getType() {
		return type;
	}

	public final Inventory getInventory(){
		return inv;
	}
	
	public int getAgentId(){
		return number;
	}
	
	public Inventory dispose(){
		assert alive;
		alive = false;
		Inventory old = this.inv;
		this.inv = new Inventory();
		return old;
	}
	
	protected final IStock getStock(Good good) {
		return inv.getStock(good);
	}

	public final IStock getMoney() {
		return inv.getStock(SimConfig.MONEY);
	}

	public final void collectDailyEndowment() {
		assert alive;
		inv.deprecate();
		inv.receive(end.getDaily());
	}
	
	@Override
	public int hashCode(){
		return number;
	}
	
	@Override
	public boolean equals(Object o){
		return ((Agent)o).number == number;
	}
	
	@Override
	public Agent clone(){
		try {
			Agent klon = (Agent) super.clone();
			klon.inv = inv.duplicate();
			return klon;
		} catch (CloneNotSupportedException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public String toString() {
		return getType() + " with " + inv;
	}

	public void refreshRef() {
		this.ref.set(this);
	}
	
}
