package com.agentecon.market;

import com.agentecon.util.Average;
import com.agentecon.util.MovingAverage;

public class GoodStats {
	
	private Average current;
	private Average yesterday;
	private MovingAverage moving;

	public GoodStats() {
		this.current = new Average();
		this.yesterday = new Average();
		this.moving = new MovingAverage();
	}
	
	public MovingAverage getMovingAverage(){
		return moving;
	}
	
	public Average getYesterday(){
		return yesterday;
	}
	
	void resetCurrent(){
		this.current = new Average();
	}
	
	void commitCurrent(){
		this.moving.add(current.getAverage());
		this.yesterday = current;
		this.current = new Average();
	}

	void notifyTraded(double quantity, double price) {
		this.current.add(quantity, price);
	}

}
