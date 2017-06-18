/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.AgentFactory;
import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.classloader.AgentLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.firm.IFirm;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProductionFunction;

public class AgentFactoryMultiplex implements IAgentFactory {
	
	private int current;
	private IAgentFactory[] factories;
	
	public AgentFactoryMultiplex(AgentLoader... loaders) throws IOException{
		this.factories = new IAgentFactory[loaders.length];
		for (int i=0; i<loaders.length; i++){
			this.factories[i] = loaders[i].loadAgentFactory();
		}
		this.current = 0;
	}
	
	public AgentFactoryMultiplex(IAgentFactory... factories){
		this.factories = factories;
		this.current = 0;
	}
	
	private IAgentFactory getCurrent(){
		return factories[current++ % factories.length];
	}

	@Override
	public IConsumer createAutarkicConsumer(Endowment endowment, IUtility utilityFunction, IProductionFunction production) {
		return getCurrent().createAutarkicConsumer(endowment, utilityFunction, production);
	}

	@Override
	public IConsumer createConsumer(Endowment endowment, IUtility utilityFunction) {
		return getCurrent().createConsumer(endowment, utilityFunction);
	}

	@Override
	public IProducer createProducer(Endowment endowment, IProductionFunction prodFun) {
		return getCurrent().createProducer(endowment, prodFun);
	}

	@Override
	public IFirm createFirm(Endowment endowment) {
		return getCurrent().createFirm(endowment);
	}
	
	public static final AgentFactoryMultiplex createDefault() throws SocketTimeoutException, IOException{
		return new AgentFactoryMultiplex(
//				new AgentLoader("meisserecon", "agentecon", "master"),
//				new AgentLoader("meisserecon", "agentecon", "master"),
//				new AgentLoader("meisserecon", "agentecon", "master"),
				new AgentFactory()
				);
	}

}
