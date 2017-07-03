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

import com.agentecon.data.JsonData;
import com.agentecon.util.LogClock;

public abstract class WebApiMethod {
	
	public final static String PLACE_HOLDER = "#NAME";
	
	public String name;
	public String examplePath;
	
	public WebApiMethod(){
		String className = getClass().getSimpleName();
		this.name = className.substring(0, className.length() - "Method".length()).toLowerCase();
		this.examplePath = createExamplePath();
	}

	protected String createExamplePath() {
		return this.name;
	}

	public String getKeyword() {
		return name;
	}

	public final JsonData execute(StringTokenizer path, Parameters params) throws IOException {
		LogClock clock = new LogClock();
		try {
			return doExecute(path, params);
		} finally {
			clock.time("Executed " + toString() + " with " + params);
		}
	}
	
	protected abstract JsonData doExecute(StringTokenizer path, Parameters params) throws IOException;

	@Override
	public String toString(){
		return name;
	}
	
}
