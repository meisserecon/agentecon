// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.goods;

//Immutable
public class Good implements Comparable<Good> {
	
	public static final Good MONEY = new Good("Taler");
	
	private String name;
	private double persistence;

	public Good(String name){
		this(name, 1.0);
	}
	
	/**
	 * Creates the good with the given name.
	 * 
	 * Persistence indicates what fraction of the good is preserved over night.
	 * For example, if persistence is 0.90, 10% of the good will vanish over night
	 * from every inventory, except if it was freshly produced. Freshly produced
	 * goods do not deprecate during the first night.
	 */
	public Good(String name, double persistence){
		this.name = name;
		this.persistence = persistence;
	}

	public String getName() {
		return name;
	}
	
	public double getPersistence(){
		return persistence;
	}
	
	@Override
	public boolean equals(Object o){
		return name.equals(((Good)o).name);
	}
	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
	
	public String toString(){
		return name;
	}
	
	public String toString_(){
		return toString().replace(' ', '_');
	}
	
	@Override
	public int compareTo(Good o) {
		return name.compareTo(o.name);
	}

}
