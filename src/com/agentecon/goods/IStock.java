package com.agentecon.goods;

public interface IStock {

	public Good getGood();

	public double getAmount();

	public double consume();

	public void remove(double quantity);

	public void add(double quantity);
	
	/**
	 * Will be protected from overnight depreciation
	 */
	public void addFreshlyProduced(double quantity);

	public void transfer(IStock source, double amount);

	public void absorb(IStock s);

	/**
	 * Returns a reference to this stock with an absolute part of the amount hidden
	 */
	public IStock hide(double amount);

	/**
	 * Returns a reference to this stock with a relative part of the amount hidden.
	 * According fractions of added reserves are also hidden.
	 */
	public IStock hideRelative(double fraction);
	
	public boolean isEmpty();

	public void deprecate();
	
	public IStock duplicate();

	
}