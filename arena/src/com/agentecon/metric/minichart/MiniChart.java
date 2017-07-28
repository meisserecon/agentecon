package com.agentecon.metric.minichart;

import java.io.IOException;

import com.agentecon.goods.Good;
import com.agentecon.metric.series.Line;
import com.agentecon.metric.series.Point;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;
import com.agentecon.web.query.AgentQuery;

public abstract class MiniChart {

	public static final int LENGTH = 100;

	private Line data;

	public MiniChart() {
		this.data = new Line();
	}

	public MiniChartData getData(int day, SimulationStepper stepper, int height) throws IOException {
		int start = Math.max(1, day - LENGTH + 1);
		for (int current = start; current <= day; current++) {
			if (!data.has(current)) {
				data.add(new Point(current, getData(stepper, current)));
			}
		}
		return new MiniChartData(getName(), start, Math.min(LENGTH, day), data, height);
	}

	protected abstract float getData(SimulationStepper stepper, int day) throws IOException;

	protected abstract String getName();

	class MiniChartData extends JsonData {

		public String name;
		public float min, max;
		public short[] data;

		public MiniChartData(String name, int start, int length, Line data, int height) {
			this.name = name;
			this.min = Float.MAX_VALUE;
			this.max = 0.0f;
			float[] temp = new float[length];
			for (int i = 0; i < length; i++) {
				float value = data.get(start + i);
				this.min = Math.min(min, value);
				this.max = Math.max(max, value);
				temp[i] = value;
			}
			this.data = new short[length];
			float range = max - min;
			for (int i = 0; i < length; i++) {
				this.data[i] = (short) ((temp[i] - min) / range * height);
				assert this.data[i] >= 0 && this.data[i] <= height;
			}
		}

	}

	public static MiniChart create(String selection) {
		int comma = selection.indexOf(',');
		if (comma >= 0) {
			AgentQuery source = new AgentQuery(selection.substring(0, comma));
			int secondComma = selection.indexOf(',', comma + 1);
			AgentQuery dest = new AgentQuery(selection.substring(comma + 1, secondComma));
			Good good = new Good(selection.substring(secondComma + 1));
			return new TradeMiniChart(source, dest, good);
		} else {
			return new NodeMiniChart(new AgentQuery(selection));
		}
	}

}
