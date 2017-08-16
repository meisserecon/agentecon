/**
 * Created by Luzius Meisser on Jun 15, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import com.agentecon.util.LogClock;
import com.agentecon.web.data.JsonData;

public abstract class WebApiMethod {

	public final static String PLACE_HOLDER = "#NAME";

	public String name;
	public String examplePath;

	public WebApiMethod() {
		this.name = deriveName();
		this.examplePath = createExamplePath();
	}

	protected String deriveName() {
		String className = getClass().getSimpleName();
		return className.substring(0, className.length() - "Method".length()).toLowerCase();
	}

	protected String createExamplePath() {
		return getName();
	}

	public final String getName() {
		return name;
	}

	public final Response execute(IHTTPSession session) throws IOException {
		LogClock clock = new LogClock();
		Parameters params = new Parameters(session);
		try {
			return execute(session, params);
		} finally {
			clock.time("Executed " + session.getUri() + "?" + session.getQueryParameterString());
		}
	}

	public Response execute(IHTTPSession session, Parameters params) throws IOException {
		JsonData answer = getJsonAnswer(params);
		return serveJson(session, answer);
	}

	protected Response serveJson(IHTTPSession session, JsonData json) {
		Response res = Response.newFixedLengthResponse(Status.OK, "application/json", json.getJson());
		res.addHeader("Access-Control-Allow-Origin", "*");
		return res;
	}

	protected JsonData getJsonAnswer(Parameters params) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String toString() {
		return name;
	}

}
