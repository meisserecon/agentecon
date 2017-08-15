<template>
  <div class="tradeview">
    <h1>Tradeview</h1>

    <div class="tradeview__wrapper" v-if="loaded">
      <div class="tradeview__main">
        <h2>Time control</h2>
        <div class="controls__time">
          <el-button-group>
            <el-button type="primary" v-if="simDay > 0" @click="prevStep"><svg width="11" height="9" viewBox="0 0 11 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M10.014 0v8.935L2 4.077zM0 0h2v9H0z"/></svg></el-button>
            <el-button type="primary" v-if="simDay < simLength" @click="nextStep"><svg width="11" height="9" viewBox="0 0 11 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M.986 0v8.935L9 4.077zM11 0H9v9h2z"/></svg></el-button>
            <el-button type="primary" v-if="simDay < simLength" @click="playing = !playing">
              <svg v-if="playing" width="9" height="9" viewBox="0 0 6 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M0 0h2v9H0zM4 0h2v9H4z"/></svg>
              <svg v-if="!playing" width="9" height="9" viewBox="0 0 9 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M.986 0v8.935L9 4.077z" /></svg>
            </el-button>
            <el-button type="primary" v-if="simDay > 0" @click="simDay = 0"><svg width="16" height="9" viewBox="0 0 16 9" xmlns="http://www.w3.org/2000/svg"><path fill="#fff" d="M15.014 0v8.935L7 4.077z"/><path fill="#fff" d="M8.014 0v8.935L0 4.077z"/></svg></el-button>
          </el-button-group>
          <el-input class="controls__day" v-model.number="simDay"></el-input>
        </div>
        <tradegraph class="tradeview__tradechart" :graphdata="tradeGraphData" :selectednode="selectedNode" @nodeclicked="handleNodeClicked" @addminichart="handleAddMinichart"  @showinfo="handleShowInfo" @showchildren="handleShowChildren"></tradegraph>
      </div>
      <div class="tradeview__side">
        <div class="controls__download">
        <h2>Document download</h2>
          <div class="controls__download-wrapper">
            <el-select  v-model="selectedMetric">
              <template v-for="option in metrics">
                <el-option>{{ option }}</el-option>
              </template>
            </el-select>
            <el-button type="primary" v-if="selectedMetric" :href="`${apiURL}/downloadcsv?metric=${selectedMetric}&sim=${simId}&day=${simDay}&agents=${simAgents}&step=${simStep}`" target="_blank">Download</el-button>
          </div>
        </div>
        <div class="tradeview__minicharts">
          <h2>Mini charts</h2>
          <div class="tradeview__minicharts-wrapper">
            <minicharts :agents="miniCharts" :simulationday="simDay" :simulationid="simId"></minicharts>
          </div>
        </div>
      </div>
      <childselection :show.sync="showChildSelection" :childrenof="childrenOf" :activenodes="simAgents" :simulationday="simDay" :simulationid="simId" @setactivenodes="handleSetActiveNodes"></childselection>
      <nodeinfo :show.sync="showNodeInfo" :agent="infoOf" :simulationday="simDay" :simulationid="simId"></nodeinfo>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3';
import Vue from 'vue';
import { Button, ButtonGroup, Input, Select } from 'element-ui';
// import Plotly from 'plotly.js/dist/plotly';
import Tradegraph from '@/components/Tradegraph';
import Childselection from '@/components/Childselection';
import Nodeinfo from '@/components/Nodeinfo';
import Minicharts from '@/components/Minicharts';
import config from '../config';

Vue.use(Button);
Vue.use(ButtonGroup);
Vue.use(Input);
Vue.use(Select);

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
      // this.miniCharts = this.miniCharts.filter(el => el !== node);
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
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

$white:                                                 #fff

.tradeview
  display: flex
  flex-direction: column

  &__wrapper
    display: flex

  &__main
    position: relative
    z-index: 10
    flex-basis: 0
    flex-grow: 1

  &__side
    position: relative
    z-index: 20
    flex-basis: 300px

  &__minicharts
    margin-top: 40px

    &-wrapper
      min-height: 300px
      border: 1px solid $border-color
      border-radius: 5px
      background-color: $white

  &__tradechart
    position: relative
    z-index: 10

.sidebar
  flex-basis: 300px
  box-sizing: border-box
  border: 3px solid black

.controls

  &__time
    position: relative
    z-index: 20
    display: flex

    .el-input
      width: 80px
      margin-left: 10px

      > input
        text-align: right

  &__download

    &-wrapper
      display: flex

    .el-button
      margin-left: 10px
</style>
