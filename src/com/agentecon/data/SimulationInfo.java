// Created on Jun 8, 2015 by Luzius Meisser

package com.agentecon.data;

import java.nio.file.attribute.FileTime;
import java.util.Date;

import com.agentecon.metric.series.Chart;
import com.agentecon.runner.Checksum;
import com.agentecon.runner.SimulationRunner;

public class SimulationInfo {

	private static final int MAX_OUTPUT_LEN = 10000;

	private long id;
	private long[] chartids;
	private int serverVersion;

	private String hash;
	private String name;
	private String description;
	private String sourceUrl;

	private int currentRound, rounds;
	private long lastRunStarted, lastRunEnded, lastProgress;

	private String output;

	private SimulationInfo(long id) {
		this.id = id;
		this.chartids = new long[] {};
		this.serverVersion = SimulationRunner.VERSION;
		this.lastRunEnded = 0;
		this.lastRunStarted = 0;
		this.lastProgress = 0;
	}
	
	public SimulationInfo(Checksum checksum, String name, FileTime date){
		this(checksum.generateId());
		this.name = name;
		this.description = "Generated from local jar file on " + date;
		this.hash = checksum.toString();
		this.sourceUrl = "";
	}

	public SimulationInfo(GitSimulationHandle handle) {
		this(handle.getId());
		this.name = handle.getName();
		this.hash = handle.getHash();
		this.sourceUrl = handle.getSourceUrl();
		this.description = handle.getDescription();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getCompletionPercent() {
		if (lastRunEnded != 0) {
			return 100;
		} else if (rounds == 0) {
			return 0;
		} else {
			return currentRound * 100 / rounds;
		}
	}

	public boolean isMaster() {
		return name.equals("master");
	}

	public String getPermanentId() {
		return isMaster() ? hash : name;
	}

	public long getStartDate() {
		return lastRunStarted;
	}

	public long getEndDate() {
		return lastRunEnded;
	}

	public long[] getChartIds() {
		return chartids;
	}

	public void triggerRun() {
		this.lastRunStarted = 0;
		this.lastRunEnded = 0;
		this.lastProgress = 0;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public boolean shouldRun() {
		return serverVersion < SimulationRunner.VERSION || hasError() || ((lastRunEnded == 0 || chartids == null) && !isRunning());
	}

	private boolean isRunning() {
		long diff = System.currentTimeMillis() - lastProgress;
		return diff < 10 * 60 * 1000;
	}

	public long[] recycleChartIds(Chart[] charts) {
		long[] toDel = new long[] {};
		if (this.chartids != null) {
			if (chartids.length > charts.length) {
				toDel = new long[chartids.length - charts.length];
				System.arraycopy(chartids, charts.length, toDel, 0, toDel.length);
				long[] newIds = new long[charts.length];
				System.arraycopy(chartids, 0, newIds, 0, newIds.length);
				this.chartids = newIds;
			}
			assert charts.length >= chartids.length; // TODO: delete old charts
			for (int i = 0; i < charts.length && i < chartids.length; i++) {
				charts[i].initId(chartids[i]);
			}
		}
		return toDel;
	}

	public void notifyWorkerStarted(int serverVersion) {
		this.serverVersion = serverVersion;
		this.lastRunStarted = System.currentTimeMillis();
		this.lastProgress = lastRunStarted;
		this.lastRunEnded = 0;
		this.output = "Run started at " + new Date(lastRunStarted);
	}

	public void notifyStarted(int rounds) {
		this.rounds = rounds;
	}

	public void notifyProgress(int day, String output, Chart... charts) {
		int len = output.length();
		if (len > MAX_OUTPUT_LEN) {
			output = output.substring(len - 10000, len);
		}
		this.output = output;
		this.currentRound = day;
		this.lastProgress = System.currentTimeMillis();
		if (this.chartids == null) {
			this.chartids = new long[] {};
		}
		if (charts.length > chartids.length) {
			long[] ids = new long[charts.length];
			System.arraycopy(chartids, 0, ids, 0, chartids.length);
			for (int i = chartids.length; i < charts.length; i++) {
				ids[i] = charts[i].getId();
			}
			this.chartids = ids;
		}
	}
	
	public boolean hasError(){
		return output != null && output.startsWith("Error while accessing");
	}
	
	public void notifyEnded(String output, Chart... charts) {
		notifyProgress(rounds, output, charts);
		this.lastRunEnded = lastProgress;
	}

	public String getOutput() {
		return output;
	}

	public long getId() {
		return id;
	}

}
