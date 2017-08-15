<template>
  <div class="tradeview">
    <h1>Tradeview</h1>

    <div v-if="loaded">
      <div class="controls">
        <div class="controls__wrapper">
          <button class="btn controls__btn" v-if="simDay > 0" @click="prevStep"><svg width="10px" height="10px" viewBox="0 0 52 49" version="1.1" xmlns="http://www.w3.org/2000/svg"><path d="M6.631,0.963 C10.293,0.963 13.262,3.932 13.262,7.594 L13.262,22.81 C13.546,22.486 13.878,22.201 14.262,21.979 L45.532,3.926 C46.77,3.211 48.294,3.211 49.532,3.926 C50.768,4.64 51.532,5.961 51.532,7.39 L51.532,43.495 C51.532,44.924 50.768,46.245 49.532,46.958 C48.913,47.315 48.221,47.495 47.532,47.495 C46.842,47.495 46.151,47.315 45.532,46.958 L14.262,28.905 C13.878,28.684 13.546,28.399 13.262,28.075 L13.262,41.938 C13.262,45.6 10.293,48.569 6.631,48.569 C2.969,48.569 0,45.6 0,41.938 L0,7.594 C0,3.932 2.969,0.963 6.631,0.963 Z"></path></svg></button>
          <button class="btn controls__btn" v-if="simDay < simLength" @click="nextStep"><svg width="10px" height="10px" viewBox="0 0 52 49" version="1.1" xmlns="http://www.w3.org/2000/svg"><path d="M44.9,0.963 C41.238,0.963 38.269,3.932 38.269,7.594 L38.269,22.81 C37.984,22.486 37.652,22.201 37.269,21.979 L6,3.926 C4.762,3.211 3.238,3.211 2,3.926 C0.763,4.64 0,5.961 0,7.39 L0,43.494 C0,44.924 0.763,46.244 2,46.959 C2.619,47.315 3.311,47.494 4,47.494 C4.691,47.494 5.381,47.315 6,46.959 L37.269,28.906 C37.652,28.683 37.984,28.398 38.269,28.074 L38.269,41.937 C38.269,45.599 41.238,48.568 44.9,48.568 C48.562,48.568 51.531,45.599 51.531,41.937 L51.531,7.594 C51.531,3.932 48.562,0.963 44.9,0.963 Z"></path></svg></button>
          <button class="btn controls__btn" v-if="simDay < simLength" @click="playing = !playing">
            <svg v-if="playing" width="9px" height="10px" viewBox="0 0 9 10" version="1.1" xmlns="http://www.w3.org/2000/svg"><path d="M2.91479167,8.53666667 C2.91479167,9.29958333 2.29625,9.918125 1.53333333,9.918125 L1.53333333,9.918125 C0.770416667,9.918125 0.151875,9.29958333 0.151875,8.53666667 L0.151875,1.38145833 C0.151875,0.618541667 0.770416667,0 1.53333333,0 L1.53333333,0 C2.29625,0 2.91479167,0.618541667 2.91479167,1.38145833 L2.91479167,8.53666667 Z" id="Shape"></path><path d="M8.099375,8.53666667 C8.099375,9.29958333 7.48083333,9.918125 6.71791667,9.918125 L6.71791667,9.918125 C5.955,9.918125 5.33645833,9.29958333 5.33645833,8.53666667 L5.33645833,1.38145833 C5.33666667,0.618541667 5.95520833,0 6.71791667,0 L6.71791667,0 C7.48083333,0 8.099375,0.618541667 8.099375,1.38145833 L8.099375,8.53666667 Z"></path></svg>
            <svg v-if="!playing" width="10px" height="10px" viewBox="0 0 43 48" version="1.1" xmlns="http://www.w3.org/2000/svg"><path d="M41.331,21.237 L5.233,0.397 C4.316,-0.132 3.189,-0.132 2.273,0.397 C1.357,0.925 0.793,1.902 0.793,2.96 L0.793,44.644 C0.793,45.702 1.357,46.679 2.273,47.207 C2.731,47.475 3.242,47.604 3.753,47.604 C4.264,47.604 4.775,47.471 5.233,47.207 L41.331,26.367 C42.249,25.838 42.81,24.861 42.81,23.803 C42.81,22.745 42.247,21.767 41.331,21.237 Z"></path></svg>
          </button>
          <button class="btn controls__btn" v-if="simDay > 0" @click="simDay = 0"><svg width="14px" height="8px" viewBox="0 0 67 39" version="1.1" xmlns="http://www.w3.org/2000/svg"><path d="M1.609,16.368 L29.19,0.443 C30.186,-0.131 31.412,-0.131 32.408,0.443 C33.404,1.017 34.017,2.082 34.017,3.23 L34.017,16.302 L61.483,0.443 C62.48,-0.131 63.705,-0.131 64.701,0.443 C65.697,1.017 66.31,2.082 66.31,3.23 L66.31,35.079 C66.31,36.229 65.697,37.292 64.701,37.866 C64.203,38.153 63.648,38.298 63.094,38.298 C62.54,38.298 61.983,38.153 61.485,37.866 L34.017,22.009 L34.017,35.079 C34.017,36.229 33.404,37.292 32.408,37.866 C31.91,38.153 31.354,38.298 30.799,38.298 C30.244,38.298 29.688,38.153 29.191,37.866 L1.609,21.942 C0.613,21.368 0,20.303 0,19.155 C0,18.007 0.613,16.942 1.609,16.368 Z"></path></svg></button>
          <input class="form-control controls__input" v-model.number="simDay">
          <select class="form-control controls__select" v-model="selectedMetric">
            <template v-for="option in metrics">
              <option>{{ option }}</option>
            </template>
          </select>
          <a class="btn" v-if="selectedMetric" :href="`${apiURL}/downloadcsv?metric=${selectedMetric}&sim=${simId}&day=${simDay}&agents=${simAgents}&step=${simStep}`" target="_blank">Download</a>
        </div>
      </div>

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
// import Plotly from 'plotly.js/dist/plotly';
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
      this.goToNewURL();
    },
    simAgents() {
      this.goToNewURL();
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
    goToNewURL() {
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
      this.goToNewURL();
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
.tradeview
  display: flex
  flex-direction: column

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

.controls
  display: inline-block
  margin-bottom: 20px
  padding: 10px
  border: 1px solid silver
  border-radius: 4px

  &__wrapper
    display: flex

  &__btn
    display: flex
    justify-content: center
    align-items: center
    width: 38px
    padding: 0
    letter-spacing: -3px
    margin-right: 5px

  &__input
    width: 70px
    padding-top: 4px
    padding-bottom: 4px
    margin-right: 5px

  &__select
    margin-right: 5px

</style>
