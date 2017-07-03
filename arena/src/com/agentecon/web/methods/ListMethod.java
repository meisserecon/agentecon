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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.agentecon.classloader.SimulationHandle;
import com.agentecon.data.JsonData;
import com.agentecon.runner.SimulationLoader;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.SimulationCache;

public class ListMethod extends WebApiMethod {

	private transient HashMap<String, SimulationHandle> handles;
	private transient SimulationCache simulations;

	public ListMethod() {
		this.handles = new HashMap<>();
		this.simulations = new SimulationCache();
	}

	public void add(SimulationHandle handle) {
		this.handles.put(handle.getPath(), handle);
	}
	
	public boolean hasSimulation(String owner, String repo, String name) {
		return handles.containsKey(owner + "/" + repo + "/" + name);
	}

	public SimulationStepper getSimulation(StringTokenizer subPath) throws IOException{
		String path = subPath.nextToken() + "/" + subPath.nextToken() + "/" + subPath.nextToken();
		SimulationHandle handle = handles.get(path);
		return simulations.getSimulation(handle);
	}

	@Override
	public JsonData doExecute(StringTokenizer path, Parameters params) {
		return new SimulationList(handles.values());
	}

	class SimulationList extends JsonData {
		
		public Collection<SimulationInfo> sims = new ArrayList<>();
		
		public SimulationList(Collection<SimulationHandle> collection) {
			for (SimulationHandle handle: collection){
				this.sims.add(new SimulationInfo(handle));
			}
		}
	}
	
	class SimulationInfo {
		
		public String owner;
		public String path;
		public String sourceUrl;

		public SimulationInfo(SimulationHandle handle) {
			this.owner = handle.getOwner();
			this.path = handle.getPath();
			this.sourceUrl = handle.getBrowsableURL(SimulationLoader.SIM_CLASS).toExternalForm();
		}
		
	}

}
