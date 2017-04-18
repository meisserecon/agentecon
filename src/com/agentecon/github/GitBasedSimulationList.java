package com.agentecon.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.agentecon.data.SimulationInfo;
import com.agentecon.runner.SimulationLoader;

public class GitBasedSimulationList {

	private static final String NAME = "\"name\":\"";

	private ArrayList<GitSimulationHandle> sims;

	public GitBasedSimulationList(HashMap<String, GitSimulationHandle> cache, String account, String repo) throws IOException, InterruptedException {
		this.sims = new ArrayList<>();

		String content = WebUtil.readHttp("https://api.github.com/repos/" + account + "/" + repo + "/tags");
		int pos = content.indexOf(NAME);
		while (pos >= 0) {
			int nameEnd = content.indexOf('"', pos + NAME.length());
			String name = content.substring(pos + NAME.length(), nameEnd);
			GitSimulationHandle handle = cache.get(name);
			if (handle == null) {
				handle = new GitSimulationHandle(account, repo, name);
				cache.put(name, handle);
			}
			sims.add(handle);
			pos = content.indexOf(NAME, pos + NAME.length());
		}
		Collections.sort(sims, new Comparator<GitSimulationHandle>() {

			@Override
			public int compare(GitSimulationHandle o1, GitSimulationHandle o2) {
				return -o1.getDate().compareTo(o2.getDate());
			}

		});
	}

	public ArrayList<GitSimulationHandle> getSims() {
		return sims;
	}

	@Override
	public String toString() {
		return sims.toString();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		GitBasedSimulationList list = new GitBasedSimulationList(new HashMap<String, GitSimulationHandle>(), "meisserecon", "Agentecon");
		System.out.println(list);
	}

}
