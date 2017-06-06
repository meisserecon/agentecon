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
	
	public boolean equals(Object o){
		return name.equals(((Good)o).name);
	}
	
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
