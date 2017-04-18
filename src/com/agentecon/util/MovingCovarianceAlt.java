package com.agentecon.util;

public class MovingCovarianceAlt {
	
	private double factor;
	private MovingAverage x, y;
	private double xy;
	
	public MovingCovarianceAlt(double factor){
		this.factor = factor;
		this.x = new MovingAverage(factor);
		this.y = new MovingAverage(factor);
		this.xy = 0.0;
	}
	
	public void add(double x, double y){
		this.x.add(x);
		this.y.add(y);
		this.xy = (1-factor) * x * y + factor * xy;
	}
	
	public double getCovariance(){
		return xy - x.getAverage() * y.getAverage();
	}
	
	public double getCorrelation(){
		return getCovariance() / Math.sqrt(x.getVariance() * y.getVariance());
	}

}
