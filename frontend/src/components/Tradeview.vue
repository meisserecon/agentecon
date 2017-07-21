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
      <a v-if="selectedMetric" :href="`${apiUrl}/downloadcsv?metric=${this.selectedMetric}&sim=${this.simId}&day=${this.simDay}&agents=${this.simAgents}&step=${this.simStep}`" target="_blank">Download</a>

      <tradegraph :graphdata="tradeGraphData" :selectednode="selectedNode" @nodeclicked="handleNodeClicked" @showinfo="handleShowInfo" @showchildren="handleShowChildren"></tradegraph>

      <childselection :show.sync="showChildSelection" :childrenof="childrenOf" :activenodes="simAgents" :simulationday="simDay" :simulationid="simId" @setactivenodes="handleSetActiveNodes"></childselection>

      <nodeinfo :show.sync="showNodeInfo" :agent="infoOf" :simulationday="simDay" :simulationid="simId"></nodeinfo>
    </div>
  </div>
</template>

<script>
import Tradegraph from '@/components/Tradegraph';
import Childselection from '@/components/Childselection';
import Nodeinfo from '@/components/Nodeinfo';
import config from '../config';

export default {
  name: 'tradeview',
  components: {
    Tradegraph,
    Childselection,
    Nodeinfo,
  },
  data() {
    return {
      apiUrl: config.apiURL,
      tradeGraphData: null,
      loaded: false,
      playing: false,
      playInterval: null,
      metrics: [],
      selectedNode: this.$route.query.selected,
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
      `${this.apiUrl}/info?sim=${this.simId}`,
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
      `${this.apiUrl}/metrics`,
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
        `${this.apiUrl}/tradegraph?sim=${this.simId}&day=${this.simDay}&agents=${this.simAgents}&step=${this.simStep}`,
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
      if (this.selectedNode && this.selectedNode === node) {
        this.selectedNode = null;
      } else {
        this.selectedNode = node;
      }
      this.goToNewDay();
    },
    handleShowInfo(node) {
      this.infoOf = node;
      this.showNodeInfo = true;
    },
    handleShowChildren(node) {
      this.childrenOf = node;
      this.showChildSelection = true;
    },
    handleSetActiveNodes(nodes) {
      this.simAgents = nodes;
    },
  },
};
</script>

<style lang="sass">

</style>
