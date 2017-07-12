/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.graph;

import java.util.ArrayList;
import java.util.Collection;

public enum ESizeType {

	UTILITY(true, false), CASH(true, true), WEALTH(true, true), PROFITS(false, true);
	
	private boolean consumer;
	private boolean firm;
	
	private ESizeType(boolean consumer, boolean firm){
		this.consumer = consumer;
		this.firm = firm;
	}
	
	public static Collection<String> getConsumerTypes(){
		ArrayList<String> list = new ArrayList<>();
		for (ESizeType t: ESizeType.values()){
			if (t.consumer){
				list.add(t.name());
			}
		}
		return list;
	}
	
	public static Collection<String> getFirmTypes(){
		ArrayList<String> list = new ArrayList<>();
		for (ESizeType t: ESizeType.values()){
			if (t.firm){
				list.add(t.name());
			}
		}
		return list;
	}
	
}
