<template>
  <div id="nodeinfo" class="context nodeinfo" data-js-context>

    <h2 class="nodeinfo__title">{{ agent }} on day {{ simulationday }}</h2>

    <div v-if="loading">Loading ...</div>

    <div v-if="!loading">
      <table>
        <tr>
          <td>Source</td>
          <td><a :href="info.source.codeLink" target="_blank">{{ info.source.owner }}</a></td>
        </tr>
        <tr v-show="info.currentUtility">
          <td>Current Utility</td>
          <td>{{ info.currentUtility }}</td>
        </tr>
        <tr v-show="info.averageUtility">
          <td>Average Utility</td>
          <td>{{ info.averageUtility }}</td>
        </tr>
        <tr v-show="info.averageDividends">
          <td>Average Dividends</td>
          <td>{{ info.averageDividends }}</td>
        </tr>
        <tr v-show="info.totalDividends">
          <td>Total Dividends</td>
          <td>{{ info.totalDividends }}</td>
        </tr>
        <tr>
          <td>No of Agents</td>
          <td>{{ info.agents }}</td>
        </tr>
        <tr>
          <th colspan="2">Inventory</th>
        </tr>
        <tr v-for="item in info.inventory">
          <td>{{ item[0] }}</td>
          <td>{{ item[1] }}</td>
        </tr>
      </table>
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
