// Created on Jun 2, 2015 by Luzius Meisser

package com.agentecon.metric;

import java.util.ArrayList;

public class AbstractListenerList<T> {

	protected ArrayList<T> list = new ArrayList<>();
	
	public AbstractListenerList() {
		super();
	}

	public void add(T l) {
		assert l != null;
		list.add(l);
	}

	@Override
	public String toString(){
		return list.toString();
	}
	
}