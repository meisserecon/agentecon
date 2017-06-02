package com.agentecon.world;

import java.util.Collection;

import com.agentecon.firm.IFirmListener;
import com.agentecon.firm.Producer;

public interface IFirms {
	
	public Collection<Producer> getRandomFirms();
	
	/**
	 * A random selection of 'cardinality' firms in random order
	 */
	public Collection<Producer> getRandomFirms(int cardinality);

	public Collection<? extends IFirmListener> getAllFirms();

}
