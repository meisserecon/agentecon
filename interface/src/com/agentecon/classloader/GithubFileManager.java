package com.agentecon.classloader;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class GithubFileManager implements JavaFileManager {
	
	private SimulationHandle handle;
	
	public GithubFileManager(SimulationHandle handle){
		this.handle = handle;
	}

	@Override
	public int isSupportedOption(String option) {
		return -1;
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
			throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean handleOption(String current, Iterator<String> remaining) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public boolean hasLocation(Location location) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
			throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling)
			throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void flush() throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void close() throws IOException {
		throw new RuntimeException("not implemented");
	}

}
