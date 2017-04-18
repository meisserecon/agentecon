package com.agentecon.finance;

import com.agentecon.good.Good;

public class Ticker extends Good {
	
	private String type;
	
	public Ticker(String type, int number) {
		super(type.substring(0, 3) + number);
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
	
}
