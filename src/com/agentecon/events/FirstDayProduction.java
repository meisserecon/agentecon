package com.agentecon.events;

import com.agentecon.firm.IFirm;
import com.agentecon.firm.IFirmListener;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.util.Average;

public class FirstDayProduction implements IProducerListener {

	private int count;
	private Average avg;
	private Good output;

	public FirstDayProduction(int firms) {
		this.count = firms * 10;
		this.avg = new Average();
	}

	@Override
	public void notifyProduced(IProducer comp, String producer, IStock[] inputs, IStock output) {
		if (count > 0) {
			assert this.output == null || this.output.equals(output.getGood());
			assert output.getAmount() < 100;
			this.output = output.getGood();
			this.avg.add(output.getAmount());
			count--;
		}
	}

	public Good getGood() {
		return output;
	}

	public double getAmount() {
		return avg.getAverage();
	}

	@Override
	public void reportResults(IProducer comp, double revenue, double cogs, double profits) {
	}

}
