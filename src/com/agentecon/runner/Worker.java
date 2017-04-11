package com.agentecon.runner;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

import com.agentecon.api.ISimulation;
import com.agentecon.data.GitSimulationHandle;
import com.agentecon.data.SimulationInfo;
import com.agentecon.data.WebSimulationJar;
import com.agentecon.html.JsonPersister;
import com.agentecon.metric.series.Chart;

// The Worker servlet should be mapped to the "/worker" URL.
public class Worker {
	
	private String owner;
	private String repo;
	private JsonPersister persister;

	public Worker(Path dataDir, String user, String repository){
		this.owner = user;
		this.repo = repository;
		this.persister = new JsonPersister(dataDir);
	}

	public void simulate(String tag) throws IOException {
		System.out.println("GitWorker triggered for " + owner + "/" + repo + "/" + tag);
		try {
			GitSimulationHandle handle = new GitSimulationHandle(owner, repo, tag);
			final SimulationInfo info = returnInfoIfWork(handle);
			if (info != null) {
				long t0 = System.nanoTime();
				System.out.println("GitWorker started for " + owner + "/" + repo + "/" + tag);
				try {
					WebSimulationJar jar = new WebSimulationJar(handle.getSimulationURL());
					ISimulation sim = jar.getClassLoader().load();
					final SimulationRunner runner = new SimulationRunner(sim);
					info.notifyStarted(sim.getConfig().getRounds());
					runner.run(new IProgressListener() {

						private static final long INTERVAL = 3000;
						private long nextRun = System.currentTimeMillis() + INTERVAL;

						@Override
						public void notifyProgress(int currentDay) {
							long now = System.currentTimeMillis();
							if (now > nextRun) {
								Chart[] charts = runner.getCharts(info.getId());
								deleteCharts(info.recycleChartIds(charts));
								if (charts.length > 0) {
									for (Chart chart : charts) {
										boolean hasId = chart.getId() != null;
										Result<?> res = ofy().save().entity(chart);
										if (!hasId) {
											res.now();
										}
									}
								}
								info.notifyProgress(currentDay, runner.getSystemOutput(), charts);
								ofy().save().entity(info);
								long now2 = System.currentTimeMillis();
								nextRun = System.currentTimeMillis() + Math.max(10 * (now2 - now), INTERVAL);
							}
						}
					});
					Chart[] charts = runner.getCharts(info.getId());
					deleteCharts(info.recycleChartIds(charts));
					for (Chart chart : charts) {
						ofy().save().entity(chart).now(); // save in separate requests to avoid 1 MB limit
					}
					info.notifyEnded(runner.getSystemOutput(), charts);

				} catch (IOException e) {
					info.notifyEnded("Error while accessing " + handle.getSimulationURL() + ": " + e.toString());
				} catch (InterruptedException e) {
					throw new java.lang.RuntimeException(e);
				}
				ofy().save().entity(info);
				long t1 = System.nanoTime();
				long diff = (t1 - t0) / 1000000;
				System.out.println("Took " + diff + "ms to run GitWorker");
			}
		} catch (RuntimeException | Error e) {
			throw e;
		} catch (InterruptedException e1) {
			throw new java.lang.RuntimeException(e1);
		}
	}

	private void deleteCharts(long[] recycleChartIds) {
		Deleter del = ofy().delete();
		for (long id: recycleChartIds){
			del.keys(Key.create(Chart.class, id));
		}
	}

	private SimulationInfo returnInfoIfWork(final GitSimulationHandle handle) {
		synchronized (this) {
			return ofy().transactNew(0, new Work<SimulationInfo>() {

				@Override
				public SimulationInfo run() {
					final SimulationInfo info = obtainInfo(handle);
					if (info.shouldRun()) {
						info.notifyWorkerStarted(SimulationRunner.VERSION);
						ofy().save().entity(info).now();
						return info;
					} else {
						return null;
					}
				}
			});
		}
	}

	private SimulationInfo obtainInfo(GitSimulationHandle handle) {
		Key<SimulationInfo> infoKey = Key.create(SimulationInfo.class, handle.getId());
		SimulationInfo info = ofy().load().key(infoKey).now();
		if (info == null) {
			info = new SimulationInfo(handle);
		}
		return info;
	}

}