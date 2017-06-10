package com.agentecon.web;

import java.io.IOException;
import java.util.List;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import com.agentecon.github.SimulationHandle;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.runner.StepGraph;
import com.google.gson.Gson;
import com.agentecon.github.GitBasedSimulationList;
import com.agentecon.github.LocalSimulationHandle;

public class SimulationServer extends FileServer {

	private GitBasedSimulationList gitList;
	private LocalSimulationHandle local;
	private SimulationCache cache;

	public SimulationServer(int port) throws IOException, InterruptedException {
		super(port);
		this.gitList = new GitBasedSimulationList("meisserecon", "Agentecon");
		this.local = new LocalSimulationHandle();
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method.equals("GET");
		String name = getParam(session, "tag");
		if (name.equals("local")) {
			return serve(session, this.local);
		} else {
			SimulationHandle handle = gitList.getSimulation(name);
			return handle == null ? super.serve(session) : serve(session, handle);
		}
	}

	private Response serve(IHTTPSession session, SimulationHandle handle) {
		try {
			SimulationStepper stepper = cache.getSimulation(handle);
			int day = getIntParam(session, "day");
			List<String> agents = session.getParameters().get("agents");
			String dataKey = getParam(session, "data");
			int stepSize = getIntParam(session, "step");
			StepGraph answer = stepper.getData(day, agents, dataKey, stepSize);
			String json = new Gson().toJson(answer);
			return Response.newFixedLengthResponse(Status.OK, getMimeTypeForFile(".json"), json);
		} catch (IOException e) {
			return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), "Failed to load " + handle + " due to " + e.toString());
		}
	}

	private int getIntParam(IHTTPSession session, String key) {
		String value = getParam(session, key);
		return value.length() == 0 ? 0 : Integer.parseInt(value);
	}

	private String getParam(IHTTPSession session, String key) {
		List<String> values = session.getParameters().get(key);
		return values != null && values.size() == 1 ? values.get(0) : "";
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		SimulationServer server = new SimulationServer(8080);
		server.run();
	}

}
