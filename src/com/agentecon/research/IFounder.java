package com.agentecon.research;

import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;

public interface IFounder extends IShareholder {
	
	public IFirm considerCreatingFirm(IInnovation research);

}
