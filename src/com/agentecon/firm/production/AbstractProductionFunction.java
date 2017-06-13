package com.agentecon.firm.production;

import java.util.Arrays;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.production.IProductionFunction;

public abstract class AbstractProductionFunction implements IProductionFunction {

	private double totInputs;
	protected final Good output;
	protected final Weight[] inputs;

	public AbstractProductionFunction(Good output, Weight... weights) {
		assert output != null;
		this.output = output;
		this.inputs = weights;
		this.totInputs = calcTotWeights();
	}
	
	@Override
	public double[] getWeights() {
		double[] ws = new double[inputs.length];
		for (int i=0; i<inputs.length; i++){
			ws[i] = inputs[i].weight;
		}
		return ws;
	}
	
	@Override
	public final double produce(Inventory inventory) {
		double production = useInputs(inventory);
		inventory.getStock(getOutput()).addFreshlyProduced(production);
		return production;
	}
	
	protected abstract double useInputs(Inventory inventory);

	private double calcTotWeights() {
		double tot = 0.0;
		for (Weight w : inputs) {
			tot += w.weight;
		}
		return tot;
	}

	public double getTotalWeight() {
		return totInputs;
	}

	@Override
	public Good[] getInput() {
		Good[] goods = new Good[inputs.length];
		for (int i=0; i<goods.length; i++){
			goods[i] = inputs[i].good;
		}
		return goods;
	}

	@Override
	public Good getOutput() {
		return output;
	}

	protected double getWeight(Good input) {
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i].good.equals(input)) {
				return inputs[i].weight;
			}
		}
		return 0;
	}
	
	@Override
	public String toString(){
		return getClass().getSimpleName() + " with weights " + Arrays.toString(inputs) + " and output " + output;
	}
	
}
