/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise1;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.goods.IStock;
import com.agentecon.learning.QuadraticMaximizer;
import com.agentecon.research.IFounder;

/**
 * A variant of the Hermit that adjusts the work amount dynamically with a primitive
 * variant of steepest descent.
 */
public class LeastSquaresHermit extends Hermit implements IFounder {

	private QuadraticMaximizer control;

	public LeastSquaresHermit(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.control = new QuadraticMaximizer(0.95, id.getRand().nextLong() + 1313, HermitConfiguration.FIXED_COSTS.getAmount(), end.getDaily()[0].getAmount());
	}
	
	@Override
	protected double calculateWorkAmount(IStock currentManhours) {
		return control.getOptimalInput();
	}

	@Override
	public double consume() {
//		System.out.println("Eating from " + getInventory());
		double utility = super.consume();
		this.control.update(utility);
		return utility;
	}

}
