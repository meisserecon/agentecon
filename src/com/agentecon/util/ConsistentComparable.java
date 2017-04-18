package com.agentecon.util;

public class ConsistentComparable<T> implements Comparable<ConsistentComparable<T>> {

	private double value;
	private T object;
	
	public ConsistentComparable(T object, double value){
		this.value = value;
		this.object = object;
	}
	
	@Override
	public int compareTo(ConsistentComparable<T> o) {
		return Double.compare(value, o.value);
	}

	public T get(){
		return object;
	}
	
}
