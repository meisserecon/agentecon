<template>
  <div>
    <template v-for="agent in agents">
      <div class="minichart__wrapper" v-if="agent && chartData[agent]">
        <h4>{{ agent }}</h4>
        <div class="minichart" v-bind:id="`minichart-${agent}`"></div>
        <!--{{ chartData[agent].name }}
        {{ chartData[agent].max }}
        {{ chartData[agent].min }}
        {{ chartData[agent].data }}-->
      </div>
    </template>
  </div>
</template>

<script>
// import * as d3 from 'd3';
import Plotly from 'plotly.js/dist/plotly';
import config from '../config';

export default {
  name: 'minicharts',
  props: ['agents', 'simulationday', 'simulationid'],
  data() {
    return {
      chartData: {},
    };
  },
  watch: {
    agents() {
      this.fetchCharts();
    },
    simulationday() {
      this.fetchCharts();
    },
  },
  methods: {
    fetchCharts() {
      // clear previous chartData
      this.chartData = {};

      // get chartData for each active agent
      this.agents.forEach((agent) => {
        fetch(
          `${config.apiURL}/minichart?sim=${this.simulationid}&day=${this.simulationday}&selection=${agent}&height=${config.miniCharts.height}`,
          config.xhrConfig,
        )
        .then(config.handleFetchErrors)
        .then(response => response.json())
        .then(
          (response) => {
            // we have to use vue's $set in order to trigger reactive updates in the view
            this.$set(this.chartData, agent, response);
          },
        )
        .then(
          () => {
            // TODO: Need a way to update the plot without drawing it entirely for each cycle
            // Updating should prevent the flash when redrawing
            const layout = {
              autosize: false,
              width: 300,
              height: 150,
              margin: {
                l: 30,
                r: 30,
                t: 0,
                b: 0,
              },
              xaxis: {
                zeroline: false,
                showticklabels: false,
              },
            };

            const data = [{
              y: this.chartData[agent].data,
              type: 'scatter',
            }];

            Plotly.newPlot(`minichart-${agent}`, data, layout, { displayModeBar: false });

            // TODO: Use restyle to update plot
            // Plotly.restyle(`minichart-${agent}`, data);
          },
        )
        .catch(error => config.alertError(error));
      });
    },
  },
};
</script>

<style lang="sass">
.minichart

  &__wrapper

</style>
