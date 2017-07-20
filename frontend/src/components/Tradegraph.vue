<template>
  <div>
    <svg id="stage" class="tradegraph" xmlns="http://www.w3.org/2000/svg"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3';

export default {
  name: 'tradegraph',
  props: ['graphdata', 'selectednode'],
  data() {
    return {
      graph: {
        stage: null,
        firmNodes: null,
        firmsTree: null,
        firmsTreeOffset: [1000, 100],
        firmsTreeDirection: +1,
        consumersNodes: null,
        consumersTree: null,
        consumersTreeOffset: [700, 100],
        consumersTreeDirection: -1,
        // object that stores coordinates of all nodes
        // used to draw links between nodes
        nodeCoordinates: {},
        NODE_RADIUS: 50,
        NODE_RADIUS_BASE: 2,
        INTER_LAYER_GAP: 50,
        INTRA_LAYER_GAP: 10,
        HORIZONTAL_GAP: 50,
      },
    };
  },
  mounted() {
    this.graph.stage = d3.select('#stage');
    this.updateTradegraph();
  },
  watch: {
    graphdata() {
      this.updateTradegraph();
    },
  },
  methods: {
    updateTradegraph() {
      this.graph.firmNodes = this.stratifyData(this.graphdata.firms);
      this.graph.consumerNodes = this.stratifyData(this.graphdata.consumers);

      // clear and updates global nodeCoordinates
      this.graph.nodeCoordinates = {};
      this.calculateNodeCoordinates(this.graph.firmNodes);
      this.calculateNodeCoordinates(this.graph.consumerNodes);

      this.drawLinks(this.graphdata.edges);

      this.drawNodes(this.graph.firmNodes);
      this.drawNodes(this.graph.consumerNodes);
    },
    addClickToNodes() {
      d3.selectAll('.node').on('click', el => this.$emit('nodeclicked', el.data.id));
    },
    stratifyData(nodeData) {
      // stratify data
      const treeData = d3.stratify()
        .id(d => d.label)
        .parentId(d => d.parent)(nodeData);

      // get nodes in hierarchical structure
      return d3.hierarchy(treeData, d => d.children);
    },
    calculateNodeCoordinates(nodeData) {
      let previousDepth = 0;
      let layerIterator = 0;
      let rootOffset;
      let horizontalDistance;
      let accumulatedLayerGap = -this.graph.INTRA_LAYER_GAP;

      // check what tree we are updating and
      // set corresponding offset and horizontal distance
      if (nodeData.data.id === 'firms') {
        rootOffset = this.graph.firmsTreeOffset;
        horizontalDistance = this.graph.firmsTreeDirection * this.graph.HORIZONTAL_GAP;
      } else if (nodeData.data.id === 'consumers') {
        rootOffset = this.graph.consumersTreeOffset;
        horizontalDistance = this.graph.consumersTreeDirection * this.graph.HORIZONTAL_GAP;
      }

      nodeData.descendants()
        .forEach((d, i) => {
          // set x coordinate
          if (d.depth === previousDepth && i !== 0) {
            layerIterator += 1;
            d.data.x = rootOffset[0] + (layerIterator * horizontalDistance);
          } else {
            d.data.x = rootOffset[0];
            layerIterator = 0;
            accumulatedLayerGap += 15;
          }
          // set y coordinate
          d.data.y = (this.graph.INTER_LAYER_GAP * i) + accumulatedLayerGap + rootOffset[1];

          // update previousDepth
          previousDepth = d.depth;

          // update nodeCoordinates for later use
          // in drawLinks function
          this.graph
            .nodeCoordinates[d.data.id] = { x: d.data.x, y: d.data.y, size: d.data.data.size };
        });
    },
    drawNodes(nodeData) {
      const self = this;
      const type = (nodeData.data.id === 'firms' ? 'firms' : 'consumers');

      d3.selectAll(`.node--${nodeData.data.id}`).remove();

      // create joins
      const nodesJoin = this.graph.stage.selectAll(`.node--${nodeData.data.id}`)
        .data(nodeData.descendants());

      // exit join
      nodesJoin.exit().remove();

      // add group elements in enter join
      const nodesEnterJoin = nodesJoin.enter();

      const group = nodesEnterJoin
        .append('g')
        .attr('id', d => d.data.id)
        .attr('class', d => `node node--${nodeData.data.id} ${(d.children ? 'branch' : 'leaf')} ${(this.selectednode && d.data.id !== this.selectednode ? 'node--out' : '')}`);

      // // append node edges
      group
        .append('path')
        .attr('class', 'node__edge')
        .attr('d', (d, i) => {
          if (i > 0) {
            return `M 0 0 L${d.parent.data.x - d.data.x} ${d.parent.data.y - d.data.y}`;
          }
          return '';
        });

      // append node to node group
      group
        .append('circle')
        .attr('class', 'node__circle')
        .attr('cx', 0)
        .attr('cy', 0);

      // append labels to node group
      group
        .append('text')
        .attr('class', 'node__text')
        .attr('text-anchor', () => {
          if (type === 'firms') {
            return 'start';
          }
          return 'end';
        })
        .text(d => d.data.id);

      // merge enter join with update
      // transform nodes to calculated position
      const groupJoin = nodesJoin
        .merge(group)
        .attr('transform', d => `translate(${self.graph.nodeCoordinates[d.data.id].x},
            ${self.graph.nodeCoordinates[d.data.id].y})`);

      groupJoin
        .select('.node__circle')
        .attr('r', d => self.graph.NODE_RADIUS_BASE * d.data.data.size);

      groupJoin
        .select('.node__text')
        .attr('dx', (d) => {
          let offsetCoefficient = -0.5;
          let offsetConstant = -0.5;
          if (type === 'firms') {
            offsetCoefficient = 0.5;
            offsetConstant = 0.5;
          }
          return ((self.graph.NODE_RADIUS_BASE * d.data.data.size) * offsetCoefficient)
            + offsetConstant;
        })
        .attr('dy', d => -5 + (-1 * (self.graph.NODE_RADIUS_BASE * d.data.data.size)));

      // add click events to nodes
      this.addClickToNodes();
    },
    drawLinks(links) {
      // remove all groups and defs
      d3.selectAll('.links__wrapper').remove();
      d3.selectAll('defs').remove();

      if (links.length > 0) {
        let currentSource = links[0].source;
        let currentDestination = links[0].destination;

        let globalSourceX = 0;
        let globalSourceY = 0;
        let globalDestinationX = 0;
        let globalDestinationY = 0;
        let deltaX = 0;
        let deltaY = 0;
        let alpha = 0;
        const xSource = 0;
        const ySource = 0;
        let localEdgeCount = 0;

        // create initial group to append links to
        let group = this.graph.stage.append('g')
          .attr('class', 'links__wrapper');

        // create the enter join
        const linksJoin = group.selectAll('.link')
          .data(links);

        // exit join
        linksJoin.exit().remove();

        const linksEnterJoin = linksJoin
          .enter();

        linksJoin
          .merge(linksEnterJoin)
          .each((d, i) => {
            if (d.source === currentSource && d.destination === currentDestination && i !== 0) {
              localEdgeCount += 1;
            } else {
              localEdgeCount = 0;
              // create new svg group
              // note: first group has already been created
              if (i !== 0) {
                group = this.graph.stage.append('g').attr('class', 'links__wrapper');
              }

              globalSourceX = this.graph.nodeCoordinates[d.source].x;
              globalSourceY = this.graph.nodeCoordinates[d.source].y;

              globalDestinationX = this.graph.nodeCoordinates[d.destination].x;
              globalDestinationY = this.graph.nodeCoordinates[d.destination].y;

              deltaX = globalDestinationX - globalSourceX;
              deltaY = globalDestinationY - globalSourceY;
              let rotationCorrection = 0;

              alpha = Math.atan(deltaY / deltaX) * (180 / Math.PI);

              if (deltaX < 0) {
                rotationCorrection = -180;
              }
              alpha += rotationCorrection;

              group
                .attr('transform', `translate(${globalSourceX}, ${globalSourceY}) rotate(${alpha})`);

              currentSource = d.source;
              currentDestination = d.destination;
            }

            const radiusSource = this.graph.NODE_RADIUS_BASE
              * this.graph.nodeCoordinates[d.source].size || this.graph.NODE_RADIUS;
            const radiusDestination = this.graph.NODE_RADIUS_BASE
              * this.graph.nodeCoordinates[d.destination].size || this.graph.NODE_RADIUS;
            const j = localEdgeCount + 2;
            const deltaXLocal = deltaX / Math.cos(alpha * Math.PI / 180);
            // 0.3 rad ^= 17.2 deg
            const x0 = xSource + (radiusSource * Math.cos((localEdgeCount + 1) * 0.3));
            const y0 = ySource - (radiusSource * Math.sin((localEdgeCount + 1) * 0.3));
            let x1 = deltaXLocal;
            x1 -= ((radiusDestination + 8) * Math.cos((localEdgeCount + 1) * 0.3));
            const y1 = -(radiusDestination + 8) * Math.sin((localEdgeCount + 1) * 0.3);

            const cx0 = j * x0;
            const cx1 = (j * (x1 - deltaXLocal)) + deltaXLocal;
            const cy0 = j * y0;
            const cy1 = j * y1;

            // append the bezier curve and marker
            group
              .append('path')
              .attr('class', 'link')
              .attr('d', `M ${x0} ${y0} C ${cx0} ${cy0}, ${cx1} ${cy1}, ${x1} ${y1}`)
              .attr('stroke-width', `${d.weight}px`)
              .attr('marker-end', () => 'url(#marker)');
          });

        // create reference marker
        this.graph.stage.append('defs')
          .append('marker')
            .attr('id', 'marker')
            .attr('class', 'marker')
            .attr('viewBox', '0 -5 10 10')
            .attr('refX', 0)
            .attr('refY', 0)
            .attr('markerWidth', 10)
            .attr('markerHeight', 10)
            .attr('orient', 'auto')
            .attr('markerUnits', 'userSpaceOnUse')
          .append('path')
            .attr('d', 'M0,-5L10,0L0,5');
      }
    },
  },
};
</script>

<style lang="sass">
$blue:                                     #33ccff
$coral:                                    #ff6557
$green:                                    #97e582
$grey:                                     #676767
$light-grey:                        rgba(0,0,0,.3)

body
  margin: 20px
  background-color: #f0f0f0

h1
  font: bold 26px/1 Helvetica, Arial, sans-serif
  text-transform: uppercase

.tradegraph
  width: 100%
  height: 1000px
  background-color: white

.node

  &__edge
    stroke-width: 1px
    opacity: .3

  &__text
    font: bold 14px/1 Helvetica, Arial sans-serif
    text-transform: uppercase
    fill: $grey

  &--firms
    .node
      &__circle
        fill: $coral
      &__edge
        stroke: $coral

  &--consumers
    .node
      &__circle
        fill: $blue
      &__edge
        stroke: $blue

  &--out
    fill-opacity: .1
    stroke-opacity: .1

  &.branch
    cursor: pointer
    .node
      &__circle
        stroke-width: 2px
    &.node
      &--firms
        .node
          &__circle
            stroke: darken($coral, 10%)
      &--consumers
        .node
          &__circle
            stroke: darken($blue, 15%)

.link
  fill: none
  stroke: $light-grey
  // animation: pulsate 3s
  animation-iteration-count: infinite
  &:hover
    stroke: $green
    cursor: pointer
  &:nth-child(1)
    animation-delay: 0.2s
  &:nth-child(2)
    animation-delay: 0.5s
  &:nth-child(3)
    animation-delay: 0.6s
  &:nth-child(4)
    animation-delay: 1s
  &:nth-child(5)
    animation-delay: 0.4s
  &:nth-child(6)
    animation-delay: 2.2s
  &:nth-child(7)
    animation-delay: 1.2s
  &:nth-child(8)
    animation-delay: 0.1s
  &:nth-child(9)
    animation-delay: 3s
  &:nth-child(10)
    animation-delay: 2.6s
  &:nth-child(11)
    animation-delay: 2s
  &:nth-child(12)
    animation-delay: 1.6s
  &:nth-child(13)
    animation-delay: 0.4s
  &:nth-child(14)
    animation-delay: 0.5s
  &:nth-child(15)
    animation-delay: 0.1s
  &:nth-child(16)
    animation-delay: 1.9s

.marker
  fill: $light-grey
  stroke-width: 2px

@keyframes pulsate
  0%
    opacity: 1
  30%
    opacity: .3
  100%
    opacity: 1
</style>
