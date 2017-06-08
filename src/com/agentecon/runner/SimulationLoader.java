// Created on May 29, 2015 by Luzius Meisser

package com.agentecon.runner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.agentecon.ISimulation;
import com.agentecon.github.WebUtil;

public class SimulationLoader extends ClassLoader {

	private static final String SIM_CLASS = "com.agentecon.Simulation";
	private static final String AGENT_CLASS = "com.agentecon.AgentFactory";

	private static final int ENDING_LEN = ".class".length();

	private Checksum checksum;
	private HashMap<String, byte[]> data;

	public SimulationLoader(Path jarFile) throws IOException {
		this(Files.readAllBytes(jarFile));
	}
	
	public SimulationLoader(InputStream input) throws SocketTimeoutException, IOException{
		this(WebUtil.readData(input));
	}
	
	public SimulationLoader(byte[] jarData) {
		super(SimulationLoader.class.getClassLoader());
		this.checksum = new Checksum(jarData);
		this.data = new HashMap<String, byte[]>();
		try {
			JarInputStream jis = new JarInputStream(new ByteArrayInputStream(jarData));
			try {
				JarEntry entry = jis.getNextJarEntry();
				while (entry != null) {
					if (!entry.isDirectory()) {
						int size = (int) entry.getSize();
						byte[] data = WebUtil.readData(size, jis);
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
		} catch (IOException e) {
			// there should be no ioexceptions when reading from memory
			throw new java.lang.RuntimeException(e);
		}
	}
	
	public Checksum getChecksum(){
		return checksum;
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

	@SuppressWarnings("unchecked")
	public Class<? extends ISimulation> loadSimClass() {
		try {
			return (Class<? extends ISimulation>) loadClass(SIM_CLASS);
		} catch (ClassNotFoundException e) {
			throw new java.lang.RuntimeException(e);
		}
	}

	public ISimulation load() throws IOException {
		try {
			return (ISimulation) loadClass(SIM_CLASS).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IOException("Failed to load simulation" , e);
		}
	}

}
