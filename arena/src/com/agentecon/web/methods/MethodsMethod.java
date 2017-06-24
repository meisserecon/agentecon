/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.agentecon.data.JsonData;

public class MethodsMethod extends WebApiMethod {
	
	private transient HashMap<String, WebApiMethod> methods;
	
	public MethodsMethod(){
		this.methods = new HashMap<>();
		this.add(this);
	}

	public void add(WebApiMethod method) {
		this.methods.put(method.getKeyword(), method);
	}

	public WebApiMethod getMethod(String name) {
		return methods.get(name);
	}

	@Override
	public JsonData execute(StringTokenizer args, Parameters params) {
		return new MethodList();
	}
	
	class MethodList extends JsonData {
		public Collection<WebApiMethod> methods = MethodsMethod.this.methods.values();
	}

}
