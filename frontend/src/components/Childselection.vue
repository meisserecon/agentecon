<template>
  <div id="childselection" class="context childselection" data-js-context>

    <h2 class="childselection__title">{{ childrenof }} on day {{ simulationday }}</h2>

    <div v-if="loading">Loading ...</div>

    <div v-if="!loading">
      <label><input type="checkbox" v-model="allSelected"></input> all</label>
      <ul class="nolist childselection__list">
        <li class="childselection__item" v-for="child in children">
          <label><input type="checkbox" :value="child" v-model="activeChildren"></input> {{ child }}</label>
        </li>
      </ul>
    </div>

    <button class="btn childselection__btn" v-if="!loading" @click="saveSelection">Save</button>
    <button class="btn childselection__btn" @click="cancelSelection">Cancel</button>
  </div>
</template>

<script>
import * as d3 from 'd3';
import config from '../config';

export default {
  name: 'childrenselection',
  props: ['show', 'childrenof', 'activenodes', 'simulationday', 'simulationid'],
  data() {
    return {
      loading: true,
      children: [],
      selectedChildren: [],
      allSelected: false,
    };
  },
  computed: {
    activeNodeArray() {
      return this.activenodes.split(',');
    },
  },
  watch: {
    show() {
      if (this.show) {
        // get children
        this.loading = true;

        fetch(
          `${config.apiURL}/children?sim=${this.simulationid}&day=${this.simulationday}&selection=${this.childrenof}`,
          config.xhrConfig,
        )
        .then(config.handleFetchErrors)
        .then(response => response.json())
        .then(
          (response) => {
            // set children
            this.children = [];
            response.children.forEach(child => this.children.push(child.label));

            // set activeChildren
            this.activeChildren = this.children.filter(x => this.activeNodeArray.includes(x));

            // set proper state of toggle all checkbox
            if (this.activeChildren.length === this.children.length) {
              this.allSelected = true;
            } else {
              this.allSelected = false;
            }

            this.loading = false;
          },
        )
        .catch(error => config.alertError(error));
      }
    },
    allSelected() {
      if (this.allSelected) {
        this.activeChildren = this.children;
      } else {
        this.activeChildren = [];
      }
    },
  },
  methods: {
    saveSelection() {
      // construct new set of active nodes
      // take all active nodes without children
      let newActiveNodes = this.activeNodeArray.filter(x => !this.children.includes(x));
      // push active children to new set
      newActiveNodes = newActiveNodes.concat(this.activeChildren);

      this.$emit('setactivenodes', newActiveNodes.join());

      this.hideChildselection();
      this.$emit('update:show', false);
    },
    cancelSelection() {
      this.hideChildselection();
      this.$emit('update:show', false);
    },
    hideChildselection() {
      d3.select('#childselection')
        .classed('in', false)
        .style('left', null);
    },
  },
};
</script>
<style lang="sass">
.childselection

  &__title
    margin: 0

  &__list
    display: flex
    flex-wrap: wrap

  &__item
    display: inline-block

  &__btn
    margin-top: 10px
</style>
