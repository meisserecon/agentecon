package com.agentecon.github;

import java.io.IOException;
import java.io.InputStream;

public abstract class SimulationHandle {

	private String owner, name;

	public SimulationHandle(String owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
	
	@Override
	public String toString(){
		return name;
	}

	public abstract InputStream open() throws IOException;

}
