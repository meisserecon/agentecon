/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;
import java.util.StringTokenizer;

import com.agentecon.ISimulation;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.runner.SimulationStepper;

public abstract class SimSpecificMethod extends WebApiMethod {
	
	private transient ListMethod sims;
	
	public SimSpecificMethod(ListMethod listing){
		this.sims = listing;
	}
	
	@Override
	protected String createExamplePath() {
		return new LocalSimulationHandle().getPath() + "/" + super.createExamplePath() + "?" + Parameters.DAY + "=133";
	}
	
	protected SimulationStepper getSimulation(StringTokenizer tok) throws IOException{
		return sims.getSimulation(tok);
	}

	public ISimulation getSimulation(StringTokenizer tok, int day) throws IOException{
		SimulationStepper stepper = getSimulation(tok);
		return stepper.getSimulation(day);
	}
	
}
