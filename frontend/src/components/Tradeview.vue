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
        <option value="" disabled>Please select</option>
        <template v-for="option in metrics">
          <option>{{ option }}</option>
        </template>
      </select>
      <a v-if="selectedMetric" :href="`${apiUrl}/getMetric?metric=${this.selectedMetric}&sim=${this.simId}&day=${this.simDay}&agents=${this.simAgents}&step=${this.simStep}`" target="_blank">Download</a>

      <tradegraph :graphdata="tradeGraphData"></tradegraph>
    </div>
  </div>
</template>

<script>
import Tradegraph from '@/components/Tradegraph';
import config from '../config';

export default {
  name: 'tradeview',
  components: {
    Tradegraph,
  },
  data() {
    return {
      apiUrl: config.apiURL,
      tradeGraphData: null,
      loaded: false,
      playing: false,
      playInterval: null,
      metrics: [
        'ALL',
      ],
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
        response.metrics.forEach((element) => {
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
        },
      )
      .catch(error => config.alertError(error));
    },
  },
};
</script>

<style lang="sass">

</style>
