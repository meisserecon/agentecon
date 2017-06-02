// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.consumer;

import com.agentecon.goods.Good;

public class Weight {
	
	public final Good good;
	public final double weight;

	public Weight(Good good, double weight) {
		this.good = good;
		this.weight = weight;
		assert weight != 0.0;
	}
	
	public String toString(){
		return weight + " " + good;
	}
}
