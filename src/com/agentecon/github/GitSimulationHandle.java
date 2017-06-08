package com.agentecon.github;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class GitSimulationHandle extends SimulationHandle {

	private String repo, sha, author, date, message;

	public GitSimulationHandle(String owner, String repo, String name) throws IOException, InterruptedException {
		super(owner, name);
		this.repo = repo;
		String commitUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/commits/" + name;
		String commitDesc = WebUtil.readHttp(commitUrl);
		this.sha = WebUtil.extract(commitDesc, "sha", 0);
		this.author = WebUtil.extract(commitDesc, "name", 0);
		this.date = WebUtil.extract(commitDesc, "date", 0);
		this.message = WebUtil.extract(commitDesc, "message", 0);
	}

	public String getDescription() {
		return message;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}
	
	public URL getSimulationURL() {
		try {
			return new URL("https://github.com/" + getOwner() + "/" + repo + "/raw/" + sha + "/jar/simulation.jar");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public String getSourceUrl() {
		return "https://github.com/" + getOwner() + "/" + repo + "/tree/" + getName();
	}

	@Override
	public InputStream open() throws IOException {
		return getSimulationURL().openStream();
	}
	
}
