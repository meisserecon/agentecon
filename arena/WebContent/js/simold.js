var agentecon = agentecon || {};

agentecon.front = agentecon.front || {};

agentecon.front.simid;

agentecon.front.chartids;

agentecon.front.listsim = function(sim) {
	var element = document.createElement('div');
	element.classList.add('row');
	element.innerHTML = '<a href=sim.html?id=' + sim.id + '>' + sim.name + ' from ' + new Date(sim.date).toDateString() + '</a>';
	document.getElementById('simlist').appendChild(element);
};

agentecon.front.getHandle = function(id) {
	$.getJSON("data/SimulationInfo-" + id + ".json", function(resp) {
		title = resp.name;
		document.getElementById('handle').innerHTML = '<h1>' + title + '</h1>';
		document.getElementById('desc').innerHTML = 'Commit comment: ' + resp.description;
		document.getElementById('sourcelink').innerHTML = 'Feel free to <a href="' + resp.sourceUrl
				+ '" target="_blank">browse this simulation\'s source code</a> on github or download and run it yourself.';

		if (resp.chartids) {
			for (var i = 0; i < resp.chartids.length; i++) {
				var chid = resp.chartids[i];
				if ($('#' + chid).length == 0) {
					$('#charts').append('<div id="' + chid + '" style="min-width: 310px; max-width: 1210px; height: 400px; margin: 0 auto"></div>');
				}
			}

			agentecon.front.chartids = resp.chartids;
			for (var i = 0; i < resp.chartids.length; i++) {
				agentecon.front.loadChart(resp.chartids[i]);
			}
		}

		document.getElementById('output').innerHTML = '<pre>' + resp.output + '</pre>';
	});
};

agentecon.front.loadChart = function(id) {
	$.getJSON("data/Chart-" + id + ".json", function(resp) {
		var prep = [];
		curColor = 8;
		for (var i = 0; i < resp.data.length; i++) {
			prep.push({
				name : resp.data[i].name,
				pointStart : resp.data[i].start,
				data : resp.data[i].values,
				color : Highcharts.getOptions().colors[curColor],
				visible : i < 2,
			// events : {
			// hide : function() {
			// agentecon.front.setVisible(this.name, false);
			// },
			// show : function() {
			// agentecon.front.setVisible(this.name, true);
			// }
			// },
			});
			if (resp.data[i].hasOwnProperty('minmax')) {
				prep.push({
					name : 'Range',
					data : resp.data[i].minMax,
					type : 'arearange',
					lineWidth : 0,
					linkedTo : ':previous',
					color : Highcharts.getOptions().colors[curColor],
					fillOpacity : 0.5,
				// zIndex : 0
				});
			}
			curColor++;
		}
		agentecon.front.drawChart(id, resp.name, resp.subtitle, prep, resp.stacking);
	});
};

agentecon.front.setVisible = function(label, visible) {
	for (var i = 0; i < agentecon.front.chartids.length; i++) {
		var chart = $('#' + agentecon.front.chartids[i]).highcharts();
		var series = chart.series;
		for (var j = 0; j < series.length; j++) {
			if (series[j].name == label && series[j].visible != visible) {
				if (visible) {
					series[j].show();
				} else {
					series[j].hide();
				}
			}
		}
	}
}

agentecon.front.addPlotLine = function(position) {
	for (var i = 0; i < agentecon.front.chartids.length; i++) {
		var chart = $('#' + agentecon.front.chartids[i]).highcharts();
		chart.xAxis[0].addPlotLine({
			value : position,
			color : 'red',
			width : 3,
			id : 'plotline1'
		});
	}
}

agentecon.front.removePlotLine = function() {
	for (var i = 0; i < agentecon.front.chartids.length; i++) {
		var chart = $('#' + agentecon.front.chartids[i]).highcharts();
		chart.xAxis[0].removePlotLine('plotline1');
	}
}

// agentecon.front.setExtremes = function(dataMin, dataMax, min, max) {
// for (var i = 0; i < agentecon.front.chartids.length; i++) {
// var chart = $('#' + agentecon.front.chartids[i]).highcharts();
// extr = chart.xAxis[0].getExtremes();
// min2 = min;
// max2 = max;
// if (extr.dataMin > min2) {
// min2 = extr.dataMin;
// }
// if (extr.dataMax < max2) {
// max2 = extr.dataMax;
// }
// if (extr.min != min2 || extr.max != max2) {
// chart.xAxis[0].setExtremes(min, max2);
// }
// }
// }

agentecon.front.drawChart = function(id, title, subtitle, chartdata, stacking) {
	var elem = $('#' + id);
	var chart = elem.highcharts();
	if (chart) {
		for (var i = 0; i < chartdata.length; i++) {
			chart.series[i].setData(chartdata[i].data);
		}
	} else {
		chart = {
			chart : {
				type : stacking == null ? 'line' : 'area',
				zoomType : 'x',
				panning : true,
				panKey : 'shift',
				events : {
					click : function(event) {
						agentecon.front.removePlotLine();
						agentecon.front.addPlotLine(event.xAxis[0].value);
					}
				}
			},

			title : {
				text : title
			},

			subtitle : {
				text : subtitle
			},

			yAxis : {
				floor : 0,
				title : {
					enabled : false
				}
			},

			// xAxis : {
			// events : {
			// afterSetExtremes : function(event) {
			// var extr = this.getExtremes();
			// agentecon.front.setExtremes(extr.dataMin, extr.dataMax, event.min, event.max);
			// }
			// }
			// },

			series : chartdata,

			plotOptions : {
				series : {
					marker : {
						enabled : false
					}
				}
			},

			exporting : {
				sourceWidth : 800,
				sourceHeight : 400,
				// scale: 2 (default)
				chartOptions : {
					title : null,
					subtitle : null
				}
			}

		};
		if (stacking != null) {
			chart.plotOptions.series.stacking = stacking;
		}
		elem.highcharts(chart);
	}
};
