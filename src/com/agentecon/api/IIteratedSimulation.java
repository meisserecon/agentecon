package com.agentecon.api;

public interface IIteratedSimulation {
	
	public boolean hasNext();
	
	public ISimulation getNext();
	
	public String getComment();
	
}
