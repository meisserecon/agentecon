<template>
  <div>
    <h1>Tradeview</h1>

    <div class="loading" v-if="loading">
      Loading...
    </div>

    <div class="graph" v-if="tradeGraphData">

      <table>
        <tr>
          <td>Simulation</td>
          <td>{{ simulationId }}</td>
        </tr>
        <tr>
        <td>Day</td>
          <td>{{ simulationDay }}</td>
        </tr>
        <tr>
        <td>Agents</td>
          <td>{{ simulationAgents }}</td>
        </tr>
        <tr>
        <td>Step</td>
          <td>{{ simulationStep }}</td>
        </tr>
      </table>

      <button v-if="simulationDay > 0" @click="prevDay">-1</button>
      <button v-if="simulationDay < simulationLength" @click="nextDay">+1</button>
      <button v-if="simulationDay < simulationLength" @click="playing = !playing">{{ playing ? '||' : '>' }}</button>
      <button v-if="simulationDay > 0" @click="simulationDay = 0"><<</button>

      <input v-model.number="simulationDay">

      <p>{{ tradeGraphData }}</p>
    </div>

    <tradegraph :data="tradeGraphData"></tradegraph>
  </div>
</template>

<script>
import Tradegraph from '@/components/Tradegraph';

export default {
  name: 'tradeview',
  components: {
    Tradegraph,
  },
  data() {
    return {
      // TODO: move apiURL to configuration
      // apiUrl: 'http://192.168.79.102:8080/luziu/local/local',
      apiUrl: 'http://localhost:8080/static',
      tradeGraphData: null,
      loading: false,
      playing: false,
      playInterval: null,
      simulationId: this.$route.query.sim,
      simulationDay: parseInt(this.$route.query.day, 10),
      simulationAgents: this.$route.query.agents,
      simulationStep: parseInt(this.$route.query.step, 10),
      // TODO: retrieve proper simulation length from API
      simulationLength: 25,
    };
  },
  created() {
    // fetch the data when the view is created and the data is
    // already being observed
    this.fetchData();
  },
  watch: {
    // call fetchData when the route changes
    $route: 'fetchData',
    playing() {
      if (this.playing) {
        this.playInterval = setInterval(this.nextDay, 1500);
      } else {
        clearInterval(this.playInterval);
      }
    },
    simulationDay() {
      this.goToNewDay();
    },
  },
  methods: {
    nextDay() {
      this.simulationDay = Math.min(this.simulationDay + 1, this.simulationLength);
      if (this.simulationDay === this.simulationLength) {
        this.playing = false;
      }
    },
    prevDay() {
      this.simulationDay = Math.max(this.simulationDay - 1, 0);
    },
    goToNewDay() {
      this.$router.replace({
        name: 'trades',
        query: {
          sim: this.simulationId,
          day: this.simulationDay,
          agents: this.simulationAgents,
          step: this.simulationStep,
        },
      });
    },
    fetchData() {
      // fetchData has all needed state data in URL
      const xhrConfig = {
        mode: 'cors',
      };

      this.tradeGraphData = null;
      this.loading = true;

      // TODO: error handling when something breaks on server or network
      fetch(
        // `${thisApiUrl}/tradegraph?sim=${this.simulationId}&day=
        // ${this.simulationDay}&agents=${this.simulationAgents}&step=${this.simulationStep}`,
        `${this.apiUrl}/test.json?sim=${this.simulationId}&day=${this.simulationDay}&agents=${this.simulationAgents}&step=${this.simulationStep}`,
        xhrConfig,
      ).then(
        (response) => {
          this.loading = false;
          return response.json();
        },
      ).then(
        (tradeGraphData) => {
          this.tradeGraphData = tradeGraphData;
        },
      );
    },
  },
};
</script>

<style lang="sass">

</style>
