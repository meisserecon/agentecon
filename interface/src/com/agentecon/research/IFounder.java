package com.agentecon.research;

import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;

public interface IFounder extends IShareholder {
	
	/**
	 * Every morning, before trading takes place, all agents that implement IFounder
	 * are asked to consider creating a firm.
	 * 
	 * For firms that require a production function, a production function can be
	 * obtained through the research object.
	 * 
	 * Founders should equip their firms with a basic amount of money and man-hours
	 * in order to kick-start them.
	 */
	public IFirm considerCreatingFirm(IInnovation research);

}
