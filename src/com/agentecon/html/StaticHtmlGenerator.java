package com.agentecon.html;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.agentecon.data.SimulationInfo;
import com.agentecon.metric.series.Chart;
import com.agentecon.runner.SimulationLoader;
import com.agentecon.runner.SimulationRunner;

public class StaticHtmlGenerator {

	private Path webPath;
	private JsonPersister persister;

	public StaticHtmlGenerator() {
		this.webPath = FileSystems.getDefault().getPath("Webcontent");
		this.persister = new JsonPersister(webPath.resolve("data"));
	}

	public void generateLocal(Path simulationJarFile) throws IOException {
		SimulationLoader loader = new SimulationLoader(simulationJarFile);
		SimulationInfo info = new SimulationInfo(loader.getChecksum(), loader.findName(), Files.getLastModifiedTime(simulationJarFile));
		try {
			info = (SimulationInfo) persister.load(SimulationInfo.class, info.getId());
		} catch (IOException e) {
			// no existing info found
		}
		SimulationRunner runner = new SimulationRunner(loader.load());
		runner.run(null);
		Chart[] charts = runner.getCharts(info.getId());
		long[] toDel = info.recycleChartIds(charts);
		for (long del: toDel){
			persister.delete(Chart.class, del);
		}
		for (Chart chart : charts) {
			persister.save(chart);
		}
	}

	public static void main(String[] args) throws IOException {
		StaticHtmlGenerator generator = new StaticHtmlGenerator();
		generator.generate(FileSystems.getDefault().getPath("..", "..", "Documents", "agentecon", "lib", "simulation.jar"));
	}

}
