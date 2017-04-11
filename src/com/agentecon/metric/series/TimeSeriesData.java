package com.agentecon.metric.series;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TimeSeriesData {

	private String name;
	// Store as blob to make sure Objectify does nothing inefficient with it
	private byte[] valueData;
	private byte[] minmaxData;

	@SuppressWarnings("unused")
	private TimeSeriesData() {
	}

	public TimeSeriesData(String name, ArrayList<Point> values) {
		this.name = name;
		this.valueData = new byte[values.size() * 8];
		ByteBuffer buffer = ByteBuffer.wrap(valueData);
		for (Point p : values) {
			p.writeTo(buffer);
		}
		assert buffer.remaining() == 0;
	}

	public TimeSeriesData(String name, ArrayList<Point> points, ArrayList<Point> minmax) {
		this(name, points);
		this.minmaxData = new byte[minmax.size() * 12];
		ByteBuffer buffer = ByteBuffer.wrap(minmaxData);
		for (Point p : minmax) {
			p.writeTo(buffer);
		}
		assert buffer.remaining() == 0;
	}
	
	public String getName() {
		return name;
	}

	public float[][] getValues() {
		float[][] points = new float[valueData.length / Point.LENGTH][];
		ByteBuffer buffer = ByteBuffer.wrap(valueData);
		for (int i = 0; i < points.length; i++) {
			points[i] = Point.read(buffer);
		}
		assert buffer.remaining() == 0;
		return points;
	}

	public float[][] getMinMax() {
		if (minmaxData == null) {
			return null;
		} else {
			float[][] points = new float[minmaxData.length / 12][];
			ByteBuffer buffer = ByteBuffer.wrap(minmaxData);
			for (int i = 0; i < points.length; i++) {
				points[i] = MinMaxPoint.read(buffer);
			}
			assert buffer.remaining() == 0;
			return points;
		}
	}

	public float getLastY() {
		ByteBuffer buffer = ByteBuffer.wrap(valueData);
		if (buffer.remaining() < Point.LENGTH) {
			return 0.0f;
		} else {
			buffer.position(buffer.remaining() - Point.LENGTH);
			float last = Point.read(buffer)[1];
			return last;
		}
	}

}
