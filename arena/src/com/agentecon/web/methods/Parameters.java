/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.nanohttpd.protocols.http.IHTTPSession;

public class Parameters {
	
	public static final String DAY = "day";
	public static final String SELECTION = "selection";
	
	private Map<String, List<String>> params;
	
	public Parameters(IHTTPSession session){
		this.params = session.getParameters();
	}
	
	public int getDay(){
		return getIntParam(DAY);
	}
	
	public String getSelection(){
		return getParam(SELECTION);
	}
	
	public int getIntParam(String key) {
		String value = getParam(key);
		return value.length() == 0 ? 0 : Integer.parseInt(value);
	}

	public String getParam(String key) {
		List<String> values = params.get(key);
		return values != null && values.size() == 1 ? values.get(0) : "";
	}

	public List<String> getFromCommaSeparatedList(String key) {
		ArrayList<String> list = new ArrayList<>();
		StringTokenizer tok = new StringTokenizer(getParam(key), ",");
		while (tok.hasMoreTokens()){
			list.add(tok.nextToken());
		}
		return list;
	}
	
	@Override
	public String toString(){
		return params.toString();
	}

}
