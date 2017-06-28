/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.classloader;

import java.io.IOException;
import java.util.ArrayList;

public class RemoteLoader extends ClassLoader {
	
	private String version;
	protected SimulationHandle source;
	private ArrayList<RemoteLoader> subloaders;
	
	public RemoteLoader(ClassLoader parent, SimulationHandle source) throws IOException{
		super(parent);
		this.version = source.getVersion();
		this.source = source;
		this.subloaders = new ArrayList<>();
	}
	
	public boolean isUptoDate() throws IOException{
		for (RemoteLoader sub: subloaders){
			if (!sub.isUptoDate()){
				return false;
			}
		}
		return source.getVersion().equals(version);
	}
	
	public void addDependentLoader(RemoteLoader subloader) {
		this.subloaders.add(subloader);
	}
	
	public String getOwner() {
		return source.getOwner();
	}

	public SimulationHandle getSource() {
		return source;
	}

}
