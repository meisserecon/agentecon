package com.agentecon.web;

import java.io.IOException;
import java.util.StringTokenizer;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.data.JsonData;
import com.agentecon.web.methods.AgentsMethod;
import com.agentecon.web.methods.ChildrenMethod;
import com.agentecon.web.methods.ListMethod;
import com.agentecon.web.methods.MethodsMethod;
import com.agentecon.web.methods.Parameters;
import com.agentecon.web.methods.RankingMethod;
import com.agentecon.web.methods.TradeGraphMethod;
import com.agentecon.web.methods.WebApiMethod;

public class SimulationServer extends FileServer {

	private MethodsMethod methods;
	private ListMethod simulations;

	public SimulationServer(int port) throws IOException, InterruptedException {
		super(port);
		String owner = "meisserecon";
		String repo = "agentecon";

		this.simulations = new ListMethod();
		this.simulations.add(new LocalSimulationHandle());
		this.simulations.add(new GitSimulationHandle(owner, repo, "master"));
		this.simulations.add(new GitSimulationHandle(owner, repo, "multigoodtag"));

		this.methods = new MethodsMethod();
		this.methods.add(this.simulations);
		this.methods.add(new AgentsMethod(this.simulations));
		this.methods.add(new TradeGraphMethod(this.simulations));
		this.methods.add(new ChildrenMethod(this.simulations));
		this.methods.add(new RankingMethod(this.simulations));
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method == Method.GET;
		String uri = session.getUri();
		StringTokenizer tok = new StringTokenizer(uri, "\\/");
		if (tok.hasMoreTokens()) {
			try {
				String methodName = tok.nextToken();
				WebApiMethod calledMethod = methods.getMethod(methodName);
				if (calledMethod != null) {
					JsonData answer = calledMethod.execute(tok, new Parameters(session));
					return serve(session, answer);
				} else {
					String owner = methodName;
					String repo = tok.hasMoreTokens() ? tok.nextToken() : "";
					String name = tok.hasMoreTokens() ? tok.nextToken() : "";
					if (simulations.hasSimulation(owner, repo, name)) {
						return serveSimulation(session, uri, tok);
					} else {
						return super.serve(session);
					}
				}
			} catch (IOException e) {
				return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, getMimeTypeForFile(".html"), "Failed to handle call due to " + e.toString());
			}
		} else {
			return super.serve(session);
		}
	}

	protected Response serveSimulation(IHTTPSession session, String uri, StringTokenizer tok) throws IOException {
		if (tok.hasMoreTokens()) {
			String methodName = tok.nextToken();
			WebApiMethod calledMethod = methods.getMethod(methodName);
			if (calledMethod != null) {
				return serve(session, calledMethod.execute(new StringTokenizer(uri, "\\/"), new Parameters(session)));
			} else {
				return super.serve(session, "sim.html");
			}
		} else {
			return super.serve(session, "sim.html");
		}
	}

	protected Response serve(IHTTPSession session, JsonData json) {
		Response res = Response.newFixedLengthResponse(Status.OK, getMimeTypeForFile(".json"), json.getJson());
		res.addHeader("Access-Control-Allow-Origin", "*");
		return res;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		SimulationServer server = new SimulationServer(8080);
		server.run();
	}

}
