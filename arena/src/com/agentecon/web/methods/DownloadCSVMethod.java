/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import com.agentecon.ISimulation;
import com.agentecon.metric.EMetrics;
import com.agentecon.metric.SimStats;
import com.agentecon.runner.SimulationStepper;

public class DownloadCSVMethod extends SimSpecificMethod {
	
	public static final String CHOICE_PARAMETER = "metric";

	public DownloadCSVMethod(ListMethod listing) {
		super(listing);
	}
	
	@Override
	protected String deriveName() {
		return "download.csv";
	}
	
	@Override
	protected String createExamplePath() {
		String superSample = super.createExamplePath();
		int dayIndex = superSample.indexOf(Parameters.DAY);
		if (dayIndex >= 0){
			superSample = superSample.substring(0, dayIndex);
		}
		return superSample + CHOICE_PARAMETER + "=" + EMetrics.PRODUCTION.getName();
	}
	
	@Override
	public Response execute(IHTTPSession session, Parameters params) throws IOException {
		EMetrics metric = EMetrics.parse(params.getParam(CHOICE_PARAMETER));
		SimulationStepper stepper = getSimulation(params);
		ISimulation sim = stepper.getNewSimulationInstance();
		SimStats stats = metric.createAndRegister(sim);
		sim.run();
		ByteArrayOutputStream csvData = new ByteArrayOutputStream();
		try (PrintStream writer = new PrintStream(csvData)){
			stats.print(writer, ", ");
		}
		return Response.newFixedLengthResponse(Status.OK, "text/csv", csvData.toByteArray());
	}
	
}
