package com.agentecon.html;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Random;

import com.agentecon.metric.series.Chart;
import com.google.gson.Gson;

public class JsonPersister {

	private Random rand;
	private Path baseDir;
	
	public JsonPersister() {
		this(FileSystems.getDefault().getPath("data"));
	}

	public JsonPersister(Path dir) {
		this.baseDir = dir;
		this.rand = new Random();
	}

	public Object load(Class<?> type, long id) throws IOException {
		Path file = getFile(type.getSimpleName(), id);
		String content = new String(Files.readAllBytes(file), "UTF-8");
		Gson gson = new Gson();
		return gson.fromJson(content, type);
	}

	private Path getFile(String type, long id) {
		return baseDir.resolve(type + "-" + id);
	}

	public long save(Object o) throws IOException {
		Gson gson = new Gson();
		String content = gson.toJson(o);
		String type = o.getClass().getSimpleName();
		while (true) {
			long id = rand.nextLong();
			Path path = getFile(type, id);
			if (Files.notExists(path)) {
				Files.write(path, content.getBytes("UTF-8"));
				return id;
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		JsonPersister persister = new JsonPersister();
		Chart chart = new Chart(1313, "TestChart", "Subtitle", Collections.EMPTY_LIST);
		long id = persister.save(chart);
		System.out.println("save chart under id " + id);
	}

	public void delete(Class<?> class1, long del) throws IOException {
		Files.deleteIfExists(getFile(class1.getSimpleName(), del));
	}

}
