<template>
  <div>
    <h1>Tradeview</h1>

    <div v-if="loaded">
      <button v-if="simDay > 0" @click="prevStep"><</button>

      <button v-if="simDay < simLength" @click="nextStep">></button>

      <button v-if="simDay < simLength" @click="playing = !playing">{{ playing ? '||' : '|>' }}</button>

      <button v-if="simDay > 0" @click="simDay = 0"><<</button>

      <input v-model.number="simDay">

      <select v-model="selectedMetric">
        <template v-for="option in metrics">
          <option>{{ option }}</option>
        </template>
      </select>
      <a v-if="selectedMetric" :href="`${apiURL}/downloadcsv?metric=${selectedMetric}&sim=${simId}&day=${simDay}&agents=${simAgents}&step=${simStep}`" target="_blank">Download</a>

      <div class="view">
        <div class="main">
          <tradegraph class="main__tradegraph" :graphdata="tradeGraphData" :selectednode="selectedNode" @nodeclicked="handleNodeClicked" @addminichart="handleAddMinichart"  @showinfo="handleShowInfo" @showchildren="handleShowChildren"></tradegraph>
          <div class="sidebar">
            <minicharts :agents="miniCharts" :simulationday="simDay" :simulationid="simId"></minicharts>
          </div>
        </div>
        <childselection :show.sync="showChildSelection" :childrenof="childrenOf" :activenodes="simAgents" :simulationday="simDay" :simulationid="simId" @setactivenodes="handleSetActiveNodes"></childselection>
        <nodeinfo :show.sync="showNodeInfo" :agent="infoOf" :simulationday="simDay" :simulationid="simId"></nodeinfo>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3';
import Tradegraph from '@/components/Tradegraph';
import Childselection from '@/components/Childselection';
import Nodeinfo from '@/components/Nodeinfo';
import Minicharts from '@/components/Minicharts';
import config from '../config';

export default {
  name: 'tradeview',
  components: {
    Tradegraph,
    Childselection,
    Nodeinfo,
    Minicharts,
  },
  data() {
    return {
      apiURL: config.apiURL,
      tradeGraphData: null,
      loaded: false,
      playing: false,
      playInterval: null,
      metrics: [],
      selectedNode: this.$route.query.selected,
      miniCharts: [],
      showNodeInfo: false,
      infoOf: null,
      showChildSelection: false,
      childrenOf: null,
      selectedMetric: '',
      simId: this.$route.query.sim,
      simDay: parseInt(this.$route.query.day, 10),
      simAgents: this.$route.query.agents,
      simStep: parseInt(this.$route.query.step, 10),
      simLength: null,
    };
  },
  created() {
    // get simulation data
    this.fetchData();

    // get length of simulation
    fetch(
      `${config.apiURL}/info?sim=${this.simId}`,
      config.xhrConfig,
    )
    .then(config.handleFetchErrors)
    .then(response => response.json())
    .then(
      (response) => {
        this.simLength = response.days;
      },
    )
    .catch(error => config.alertError(error));

    // get download options
    fetch(
      `${config.apiURL}/metrics`,
      config.xhrConfig,
    )
    .then(config.handleFetchErrors)
    .then(response => response.json())
    .then(
      (response) => {
        response.metrics.forEach((element, i) => {
          if (i === 0) {
            this.selectedMetric = element;
          }
          this.metrics.push(element);
        });
      },
    )
    .catch(error => config.alertError(error));
  },
  watch: {
    // call fetchData when the route changes
    $route: 'fetchData',
    playing() {
      if (this.playing) {
        this.playInterval = setInterval(this.nextStep, 100);
      } else {
        clearInterval(this.playInterval);
      }
    },
    simDay() {
      this.goToNewDay();
    },
    simAgents() {
      this.goToNewDay();
    },
  },
  methods: {
    nextStep() {
      this.simDay = Math.min(this.simDay + this.simStep, this.simLength);
      if (this.simDay === this.simLength) {
        this.playing = false;
      }
    },
    prevStep() {
      this.simDay = Math.max(this.simDay - this.simStep, 0);
    },
    goToNewDay() {
      this.$router.replace({
        name: 'trades',
        query: {
          sim: this.simId,
          day: this.simDay,
          agents: this.simAgents,
          step: this.simStep,
          selected: this.selectedNode,
        },
      });
    },
    fetchData() {
      // fetchData has all needed state data in URL
      fetch(
        `${config.apiURL}/tradegraph?sim=${this.simId}&day=${this.simDay}&agents=${this.simAgents}&step=${this.simStep}`,
        config.xhrConfig,
      )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
        (response) => {
          this.tradeGraphData = response;
          this.loaded = true;

          // check if we got new hints on what nodes to add
          if (response.hint.length > 0) {
            this.simAgents += `,${response.hint.join()}`;
          }
        },
      )
      .catch(error => config.alertError(error));
    },
    handleNodeClicked(node) {
      this.playing = false;
      if (this.selectedNode && this.selectedNode === node) {
        this.selectedNode = null;
      } else {
        this.selectedNode = node;
      }
      this.goToNewDay();
    },
    handleAddMinichart(node) {
      // remove chart of node if it is already there
      this.miniCharts = this.miniCharts.filter(el => el !== node);
      // remove last chart if there would be more than configured
      if (this.miniCharts.length >= config.miniCharts.noOfChartsInSidebar) {
        this.miniCharts.pop();
      }
      // add chart to the top of the list
      this.miniCharts.unshift(node);
    },
    handleShowInfo(data) {
      this.infoOf = data[0];
      this.showNodeInfo = true;

      d3.selectAll('[data-js-context]')
        .style('left', null)
        .classed('in', false);

      d3.select('#nodeinfo')
        .style('left', `${data[1].x}px`)
        .style('top', `${data[1].y}px`)
        .classed('in', true);
    },
    handleShowChildren(data) {
      this.childrenOf = data[0];
      this.showChildSelection = true;

      d3.selectAll('[data-js-context]')
        .style('left', null)
        .classed('in', false);

      d3.select('#childselection')
        .style('left', `${data[1].x}px`)
        .style('top', `${data[1].y}px`)
        .classed('in', true);
    },
    handleSetActiveNodes(nodes) {
      this.simAgents = nodes;
    },
  },
};
</script>

<style lang="sass">
.main
  display: flex
  align-items: stretch

  &__tradegraph
    position: relative
    flex-basis: 0
    flex-grow: 1

.sidebar
  flex-basis: 300px
  box-sizing: border-box
  border: 3px solid black
</style>
