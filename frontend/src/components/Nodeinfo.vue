<template>
  <div v-if="show">

    <h2>Info of {{ agent }} on day {{ simulationday }}</h2>

    <div v-if="loading">Loading ...</div>

    <div v-if="!loading">
      <ul>
        <li v-for="(value, key) in info">{{ key }}: {{ value }}</li>
      </ul>
    </div>

    <button @click="closeInfo">Close</button>
  </div>
</template>

<script>
import config from '../config';

export default {
  name: 'nodeinfo',
  props: ['show', 'agent', 'simulationday', 'simulationid'],
  data() {
    return {
      loading: true,
      info: {},
    };
  },
  watch: {
    show() {
      if (this.show) {
        // get children
        this.loading = true;

        fetch(
          `${config.apiURL}/agents?sim=${this.simulationid}&day=${this.simulationday}&selection=${this.agent}`,
          config.xhrConfig,
        )
        .then(config.handleFetchErrors)
        .then(response => response.json())
        .then(
          (response) => {
            this.info = response;
            this.loading = false;
          },
        )
        .catch(error => config.alertError(error));
      }
    },
  },
  methods: {
    closeInfo() {
      this.$emit('update:show', false);
    },
  },
};
</script>
