<template>
  <div id="nodeinfo" class="context nodeinfo" data-js-context>

    <h2 class="nodeinfo__title">Info of {{ agent }} on day {{ simulationday }}</h2>

    <div v-if="loading">Loading ...</div>

    <div v-if="!loading">
      <ul class="nolist">
        <li v-for="(value, key) in info">{{ key }}: {{ value }}</li>
      </ul>
    </div>

    <button class="btn nodeinfo__btn" @click="closeInfo">Close</button>
  </div>
</template>

<script>
import * as d3 from 'd3';
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
      d3.select('#nodeinfo')
        .classed('in', false)
        .style('left', null);
      this.$emit('update:show', false);
    },
  },
};
</script>
<style lang="sass">
.nodeinfo

  &__title
    margin: 0

  &__btn
    margin-top: 10px
</style>
