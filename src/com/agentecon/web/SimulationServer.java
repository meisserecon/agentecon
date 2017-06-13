package com.agentecon.web;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.SimulationHandle;
import com.agentecon.data.AgentData;
import com.agentecon.data.TradeGraph;
import com.agentecon.github.GitBasedSimulationList;
import com.agentecon.runner.SimulationStepper;
import com.google.gson.Gson;

public class SimulationServer extends FileServer {

	private String owner, repo;
	private GitBasedSimulationList gitList;
	private LocalSimulationHandle local;
	private SimulationCache cache;

	public SimulationServer(int port) throws IOException, InterruptedException {
		super(port);
		this.owner = "meisserecon";
		this.repo = "Agentecon";
		this.gitList = new GitBasedSimulationList(owner, repo);
		this.local = new LocalSimulationHandle();
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method.equals("GET");
		String uri = session.getUri();
		StringTokenizer tok = new StringTokenizer(uri, "\\/");
		String first = tok.nextToken();
		try {
			if (first.equals(owner) && tok.nextToken().equals(repo)) {
				String tag = tok.nextToken();
				if (tag.equals("local")) {
					return serve(session, this.local, tok.nextToken());
				} else {
					SimulationHandle handle = gitList.getSimulation(tag);
					return handle == null ? super.serve(session) : serve(session, handle, tok.nextToken());
				}
			} else {
				return super.serve(session);
			}
		} catch (NoSuchElementException e) {
			return super.serve(session);
		}
	}

	private Response serve(IHTTPSession session, SimulationHandle handle, String request) {
		try {
			SimulationStepper stepper = cache.getSimulation(handle);
			switch (request) {
			case "simulation":
				// TODO: return a description of the simulation, including a list of charts
			case "chart":
				// TODO: return the data of one such charts
			case "progress":
				// TODO: return current simulation progress (highest available day)
			case "agent":{
				int day = getIntParam(session, "day");
				int agent = getIntParam(session, "agent");
				AgentData data = stepper.getAgentData(day, agent);
				return serveObjectAsJson(data);
			}
			case "tradegraph": {
				int day = getIntParam(session, "day");
				List<String> agents = session.getParameters().get("agents");
				String dataKey = getParam(session, "data");
				int stepSize = getIntParam(session, "step");
				TradeGraph answer = stepper.getData(day, agents, dataKey, stepSize);
				return serveObjectAsJson(answer);
			}
			case "ownershipgraph":
				// TODO: return the onwership graph of a given day
			default:
			}
			return super.serve(session);
		} catch (IOException e) {
			return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), "Failed to load " + handle + " due to " + e.toString());
		}
	}

	protected Response serveObjectAsJson(Object json) {
		return serveJson(new Gson().toJson(json));
	}

	protected Response serveJson(String json) {
		return Response.newFixedLengthResponse(Status.OK, getMimeTypeForFile(".json"), json);
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
