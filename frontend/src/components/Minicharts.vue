<template>
  <div>

    <template v-for="agent in agents">
      <div class="minichart__wrapper" v-if="agent" :key="`minichart-${agent}`">
        <div class="minichart__title">{{ agent }}</div>
        <div class="minichart" :id="`minichart-${agent}`"></div>
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
    agents(newVal, oldVal) {
      // find new agents
      const newCharts = newVal.filter(i => oldVal.indexOf(i) < 0);
      // wrap in timeout to make sure that template is updated with new nodes
      // before we init graphs on them
      setTimeout(() => {
        newCharts.forEach(agent => this.initChart(agent));
        this.fetchCharts();
      }, 1);
    },
    simulationday() {
      this.fetchCharts();
    },
  },
  methods: {
    initChart(agent) {
      const layout = {
        autosize: true,
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
        y: [],
        mode: 'lines',
        type: 'scatter',
      }];

      Plotly.newPlot(`minichart-${agent}`, data, layout, { displayModeBar: false });
    },
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
            const data = {
              y: [this.chartData[agent].data],
            };

            Plotly.update(`minichart-${agent}`, data);
          },
        )
        .catch(error => config.alertError(error));
      });
    },
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

.minichart

  &__title
    margin: 11px 0
    font: bold 14px/1.4 $avenir
    text-align: center

</style>
