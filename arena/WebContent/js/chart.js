var agentecon = agentecon || {};

agentecon.front = agentecon.front || {};

agentecon.front.loadChart = function(id) {
	gapi.client.simApi.getChart({
		'id' : id
	}).execute(function(resp) {
		var prep = [];
		for (var i = 0; i < resp.data.length; i++) {
			prep.push({
				name : resp.data[i].name,
				pointStart : resp.data[i].start,
				data : resp.data[i].values
			});
			if (resp.data[i].hasOwnProperty('minMax')) {
				prep.push({
					name : 'Range',
					data : resp.data[i].minMax,
					type : 'arearange',
					lineWidth : 0,
					linkedTo : ':previous',
					// color : Highcharts.getOptions().colors[0],
					fillOpacity : 0.3,
				// zIndex : 0
				});
			}
		}
		agentecon.front.drawChart(id, resp.name, resp.subtitle, prep);
	});
};

agentecon.front.drawChart = function(id, title, subtitle, chartdata) {
	$('#chart').highcharts({
		title : {
			text : title
		},

		subtitle : {
			text : subtitle
		},

		// xAxis : {
		// categories : [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul',
		// 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ]
		// },

		series : chartdata
	});
};

/**
 * Initializes the application.
 */
agentecon.front.init = function(apiRoot, id) {
	var callback = function() {
		agentecon.front.getHandle(id);
	}
	gapi.client.load('simApi', 'v1', callback, apiRoot);
};
