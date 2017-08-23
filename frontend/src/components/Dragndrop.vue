<template>
  <div>
    <h1>Drag and Drop</h1>
    <svg id="stage" class="dnd" xmlns="http://www.w3.org/2000/svg"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3';
// import config from '../config';

export default {
  name: 'dragndrop',
  components: {
  },
  data() {
    return {
      stage: null,
    };
  },
  mounted() {
    this.stage = d3.select('#stage');

    const data = [{ x: 100, y: 100 }, { x: 200, y: 200 }];

    function dragstarted() {
      d3.select(this)
        .raise()
        .classed('active', true);
    }

    function dragged(d) {
      d3.select(this)
        .attr('cx', d.x = d3.event.x)
        .attr('cy', d.y = d3.event.y);
    }

    function dragended() {
      d3.select(this)
        .classed('active', false);
    }

    const drag = d3.drag()
      .on('start', dragstarted)
      .on('drag', dragged)
      .on('end', dragended);

    this.stage.selectAll('circle')
      .data(data)
      .enter()
      .append('circle')
      .attr('cx', d => d.x)
      .attr('cy', d => d.y)
      .attr('r', 50)
      .call(drag);
  },
  watch: {
  },
  methods: {
  },
};
</script>

<style lang="sass">
.dnd
  width: 500px
  height: 500px
  background-color: #f0f0f0

circle
  &.active
    fill: gold
</style>
