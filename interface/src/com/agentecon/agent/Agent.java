package com.agentecon.agent;

import com.agentecon.classloader.RemoteLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IConsumerListener;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.sim.AgentRef;

public abstract class Agent implements IAgent, Cloneable {

	private int number;
	private String type;
	private Endowment end;
	private Inventory inv;
	private boolean alive;

	private AgentRef ref;

	public Agent(Endowment end) {
		this.type = inferType(getClass());
		this.inv = end.getInitialInventory();
		this.end = end;
		this.alive = true;
		this.number = 0;
		this.ref = new AgentRef(this);
		assert type != null;
	}
	
	private static String inferType(Class<? extends Agent> clazz) {
		ClassLoader loader = clazz.getClassLoader();
		if (loader instanceof RemoteLoader){
			return ((RemoteLoader)loader).getOwner() + "-" + getName(clazz);
		} else {
			return getName(clazz);
		}
	}
	
	private static String getName(Class<?> clazz) {
		String name = clazz.getSimpleName();
		while (name.length() == 0) {
			clazz = clazz.getSuperclass();
			name = clazz.getSimpleName();
		}
		return name;
	}

	public void setId(int id){
		assert this.number == 0;
		this.number = id;
	}
	
	public void addListener(Object listener) {
		if (listener instanceof IConsumerListener && this instanceof IConsumer) {
			((IConsumer) this).addListener((IConsumerListener) listener);
		} else {
			((IFirm) this).addFirmMonitor((IFirmListener) listener);
			if (listener instanceof IProducerListener && this instanceof IProducer) {
				((IProducer) this).addProducerMonitor((IProducerListener) listener);
			}
		}
	}

	public AgentRef getReference() {
		return ref;
	}

	public boolean isAlive() {
		return alive;
	}

	public String getName() {
		return getType() + " " + number;
	}

	public final String getType() {
		return type;
	}

	public final Inventory getInventory() {
		return inv;
	}

	public int getAgentId() {
		return number;
	}

	public Inventory dispose() {
		assert alive;
		alive = false;
		Inventory old = this.inv;
		this.inv = new Inventory(old.getMoney().getGood());
		return old;
	}

	protected final IStock getStock(Good good) {
		return inv.getStock(good);
	}

	public final IStock getMoney() {
		return inv.getMoney();
	}

	public final void collectDailyEndowment() {
		assert alive;
		inv.deprecate();
		inv.receive(end.getDaily());
	}

	@Override
	public int hashCode() {
		return number;
	}

	@Override
	public boolean equals(Object o) {
		return ((Agent) o).number == number;
	}

	@Override
	public Agent clone() {
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
