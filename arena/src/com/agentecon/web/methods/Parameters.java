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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.nanohttpd.protocols.http.IHTTPSession;

public class Parameters {

	public static final String DAY = "day";
	public static final String SIM = "sim";
	public static final String SELECTION = "selection";
	public static final String ALT_SELECTION = "agents";

	private Map<String, List<String>> params;

	public Parameters(IHTTPSession session) {
		this.params = session.getParameters();
	}

	public String getSimulation() {
		return getParam(SIM);
	}

	public int getDay() {
		return getIntParam(DAY);
	}

	public String getSingleSelection() {
		Collection<String> sel = getSelection();
		return sel.size() == 0 ? "" : sel.iterator().next();
	}

	public int getIntParam(String key) {
		return getIntParam(key, 0);
	}

	public int getIntParam(String key, int defauld) {
		String value = getParam(key);
		return value.length() == 0 ? defauld : Integer.parseInt(value);
	}

	public String getParam(String key) {
		List<String> values = params.get(key);
		return values != null && values.size() == 1 ? values.get(0) : "";
	}
	
	public String getSelectionString() {
		String sel = getParam(SELECTION);
		if (sel == null){
			sel = getParam(ALT_SELECTION);
		}
		return sel == null ? "" : sel;
	}

	public List<String> getSelection() {
		List<String> list = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(getSelectionString(), ",");
		while (tok.hasMoreTokens()) {
			list.add(tok.nextToken());
		}
		return list;
	}

	@Override
	public String toString() {
		return params.toString();
	}

}
