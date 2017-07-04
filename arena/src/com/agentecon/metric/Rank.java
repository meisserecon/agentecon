/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric;

public class Rank implements Comparable<Rank> {
	
	private String type;
	private double lifeTimeUtility;
	private transient int count;

	public Rank(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
	public void add(double lifeTimeUtility){
		this.lifeTimeUtility *= count++;
		this.lifeTimeUtility += lifeTimeUtility;
		this.lifeTimeUtility /= count;
	}
	
	@Override
	public int compareTo(Rank o) {
		return -Double.compare(lifeTimeUtility, o.lifeTimeUtility);
	}
	
	@Override
	public String toString() {
		return getType() + "\t" + lifeTimeUtility;
	}
	
}