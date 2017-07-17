/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.web.methods;

import java.io.IOException;

import com.agentecon.web.data.JsonData;

public class MetricsMethod extends WebApiMethod {

	public MetricsMethod() {
		super();
	}

	@Override
	protected JsonData doExecute(Parameters params) throws IOException {
		return new Metrics();
	}

	class Metrics extends JsonData {

		public EMetrics[] metrics = EMetrics.values();
	}

}

enum EMetrics {
	
	PRODUCTION, UTILITY;

}
