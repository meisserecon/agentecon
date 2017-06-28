/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

public class RemoteLoader extends ClassLoader {
	
	protected SimulationHandle source;
	
	public RemoteLoader(ClassLoader parent, SimulationHandle source){
		super(parent);
		this.source = source;
	}
	
	public String getOwner() {
		return source.getOwner();
	}

	public SimulationHandle getSource() {
		return source;
	}

}
