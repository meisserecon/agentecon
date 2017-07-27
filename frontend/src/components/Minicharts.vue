<template>
  <div>
    <template v-for="agent in agents">
      <div v-if="agent && chartData[agent]">
        <h4>{{ agent }}</h4>
        {{ chartData[agent].name }}
        {{ chartData[agent].max }}
        {{ chartData[agent].min }}
        {{ chartData[agent].data }}
      </div>
    </template>
  </div>
</template>

<script>
// import * as d3 from 'd3';
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
        .catch(error => config.alertError(error));
      });
    },
  },
};
</script>
