// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.agentecon.IAgentFactory;
import com.agentecon.util.IOUtils;

public class AgentLoader extends ClassLoader {

	private static final String AGENT_CLASS = "com.agentecon.AgentFactory";

	private static final int ENDING_LEN = ".class".length();

	private long date;
	private SimulationHandle source;
	private HashMap<String, byte[]> data;
	
	public AgentLoader() throws SocketTimeoutException, IOException{
		this("meisserecon", "agentecon", "master");
	}
	
	public AgentLoader(String owner, String repo, String branch) throws SocketTimeoutException, IOException{
		this(new GitSimulationHandle(owner, repo, branch));
	}
	
	public AgentLoader(File basePath) throws IOException {
		this(new LocalSimulationHandle(basePath));
	}
	
	public AgentLoader(SimulationHandle source) throws SocketTimeoutException, IOException{
		super(AgentLoader.class.getClassLoader());
		URL url = source.getJarURL();
		URLConnection conn = url.openConnection();
		this.source = source;
		this.date = conn.getLastModified() == 0 ? conn.getDate() : conn.getLastModified();
		this.data = new HashMap<String, byte[]>();
		try (InputStream is = conn.getInputStream()){
			JarInputStream jis = new JarInputStream(new BufferedInputStream(is, 500000));
			try {
				JarEntry entry = jis.getNextJarEntry();
				while (entry != null) {
					if (!entry.isDirectory()) {
						int size = (int) entry.getSize();
						byte[] data = IOUtils.readData(size, jis);
						this.data.put(toClassName(entry.getName()), data);
						jis.closeEntry();
					}
					entry = jis.getNextJarEntry();
				}
			} finally {
				jis.close();
			}
		} catch (ClassFormatError e) {
			throw new java.lang.RuntimeException(e);
		}
	}
	
	public long getDate(){
		return date;
	}
	
	public SimulationHandle getSourceData(){
		return source;
	}
	
	public static byte[] readData(InputStream source) throws IOException {
		int ava = source.available();
		int size = ava == 0 ? 1000 : ava;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		byte[] buffer = new byte[size];
		while (true) {
			int read = source.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.write(buffer, 0, read);
			} else {
				assert read == -1;
				break;
			}
		}
		return out.toByteArray();
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = this.data.remove(name);
		if (data == null) {
			throw new ClassNotFoundException(name);
		} else {
			return super.defineClass(name, data, 0, data.length);
		}
	}

	private String toClassName(String name) {
		return name.substring(0, name.length() - ENDING_LEN).replace('/', '.');
	}

	public IAgentFactory loadAgentFactory() throws IOException {
		try {
			return (IAgentFactory) loadClass(AGENT_CLASS).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException("Failed to load simulation" , e);
		}
	}
	
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		IAgentFactory fac = new AgentLoader().loadAgentFactory();
		System.out.println(fac);
	}

}
