package com.agentecon.web;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

public class FileServer extends NanoHTTPD {

	private File baseFolder;

	public FileServer(int port) {
		super(port);
		this.baseFolder = new File("WebContent");
		initMime();
	}

	protected void initMime() {
		if (mimeTypes().isEmpty()) {
			 mimeTypes().put("css", "text/css");
			 mimeTypes().put("htm", "text/html");
			 mimeTypes().put("html", "text/html");
			 mimeTypes().put("xml", "text/xml");
			 mimeTypes().put("gif", "image/gif");
			 mimeTypes().put("jpg", "image/jpeg");
			 mimeTypes().put("jpeg", "image/jpeg");
			 mimeTypes().put("png", "image/png");
			 mimeTypes().put("pdf", "application/pdf");
			 mimeTypes().put("json", "application/json");
		}
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		assert method == Method.GET;
		String uri = session.getUri();
		System.out.println(method + " requested  on " + uri);
		if (uri.equals("/")){
			uri = "/index.html";
		}
		return serve(session, uri);
	}

	protected Response serve(IHTTPSession session, String uri) {
		File child = new File(baseFolder, uri);
		if (child.isFile() && child.getAbsolutePath().startsWith(baseFolder.getAbsolutePath())) {
			try {
				return Response.newChunkedResponse(Status.OK, getMimeTypeForFile(uri), new FileInputStream(child));
			} catch (FileNotFoundException e) {
				throw new java.lang.RuntimeException(e);
			}
		} else {
			String msg = "<html><body><h1>Hello server</h1>\n";
			Map<String, String> parms = session.getParms();
			if (parms.get("username") == null) {
				msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
			} else {
				msg += "<p>Hello, " + parms.get("username") + "!</p>";
			}

			msg += "</body></html>\n";

			return Response.newFixedLengthResponse(msg);
		}
	}
	
	protected void run() throws IOException, InterruptedException {
		start();
		try {
			URI uri = new URI("http://" + (getHostname() == null ? "localhost" : getHostname()) + ":" + getListeningPort());
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(uri);
			} else {
				System.out.println("Listening on " + uri);
			}
			while (true) {
				Thread.sleep(60000);
			}
		} catch (URISyntaxException e) {
			throw new java.lang.RuntimeException(e);
		} finally {
			stop();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		FileServer server = new FileServer(8080);
		server.run();
	}

}
