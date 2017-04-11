package com.agentecon.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class GitSimulationHandle {

	private String owner, repo, name;
	private String sha, author, date, message;

	public GitSimulationHandle(String owner, String repo, String name) throws IOException, InterruptedException {
		this.owner = owner;
		this.repo = repo;
		this.name = name;
		String commitUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/commits/" + name;
		String commitDesc = WebUtil.readHttp(commitUrl);
		this.sha = WebUtil.extract(commitDesc, "sha", 0);
		this.author = WebUtil.extract(commitDesc, "name", 0);
		this.date = WebUtil.extract(commitDesc, "date", 0);
		this.message = WebUtil.extract(commitDesc, "message", 0);
	}

	public String getName() {
		return name;
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

	public long getId() {
		return toId(sha);
	}

	public static long toId(String sha) {
		return sha.hashCode();
	}

	public URL getSimulationURL() {
		try {
			return new URL("http://github.com/" + owner + "/" + repo + "/raw/" + sha + "/jar/simulation.jar");
		} catch (MalformedURLException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public String getSourceUrl() {
		return "https://github.com/" + owner + "/" + repo + "/tree/" + name;
	}

	public String toString(){
		return name;
	}

	public String getHash() {
		return sha;
	}
	
	public String getPermanentId(){
		// assume only master is dynamic, tags do not change
		return name.equals("master") ? sha : name;
	}
	
}
