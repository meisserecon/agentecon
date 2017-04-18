package com.agentecon.world;

import java.util.Collection;

import com.agentecon.api.IFirm;

public interface IFirms {
	
	public Collection<? extends IFirm> getRandomFirms();
	
	/**
	 * A random selection of 'cardinality' firms in random order
	 */
	public Collection<? extends IFirm> getRandomFirms(int cardinality);

	public Collection<? extends IFirm> getAllFirms();
	
}
