<template>
  <div>
    <h1>Tradegraph</h1>
    <p>Data: {{ graphData }}</p>
  </div>
</template>

<script>
import * as d3 from 'd3';

export default {
  name: 'tradegraph',
  data() {
    return {
      graph: {
        stage: null,
        firmsTree: null,
        firmsTreeOffset: [700, 100],
        firmsTreeDirection: +1,
        consumersTree: null,
        consumersTreeOffset: [350, 100],
        consumersTreeDirection: -1,
        // object that stores coordinates of all nodes
        // used to draw links between nodes
        nodeCoordinates: {},
        NODE_RADIUS: 50,
      },
      graphData: {
        firms: [
          { label: 'firms', children: 4, parent: '' },
          { label: 'firm0', children: 4, parent: 'firms' },
          { label: 'firm1', children: 4, parent: 'firms' },
        ],
        consumers: [
          { label: 'consumers', children: 4, parent: '' },
          { label: 'consumer0', children: 4, parent: 'consumers' },
          { label: 'consumer1', children: 4, parent: 'consumers' },
          { label: 'consumer2', children: 4, parent: 'consumers' },
          { label: 'consumer3', children: 4, parent: 'consumer1' },
          { label: 'consumer4', children: 4, parent: 'consumer3' },
          { label: 'consumer5', children: 4, parent: 'consumer3' },
        ],
        edges: [
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm0', destination: 'consumer0' },
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer0', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm1', destination: 'consumer0' },
          { label: '35 input 3 @ 3.81$', weight: 7, source: 'consumer0', destination: 'firm1' },
          { label: '35 input 3 @ 3.81$', weight: 7, source: 'consumer0', destination: 'firm1' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer2', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer0', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 5, source: 'firm0', destination: 'consumer2' },
          { label: '35 input 3 @ 3.81$', weight: 5, source: 'firm0', destination: 'consumer2' },
          { label: '35 input 3 @ 3.81$', weight: 10, source: 'firm1', destination: 'consumer2' },
          { label: '35 input 3 @ 3.81$', weight: 10, source: 'firm1', destination: 'consumer2' },
          { label: '35 input 3 @ 3.81$', weight: 6, source: 'consumer5', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm1' },
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer5', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer5', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm0' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm1', destination: 'consumer5' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm1', destination: 'consumer5' },
          { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm1', destination: 'consumer5' },
          { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm1', destination: 'consumer1' },
          { label: '35 input 3 @ 3.81$', weight: 3, source: 'firm1', destination: 'consumer5' },
          { label: '35 input 3 @ 3.81$', weight: 8, source: 'consumer4', destination: 'firm0' },
        ],
      },
    };
  },
  methods: {
    drawNodes(nodeData) {
      const log = false;
      if (log) {
        // console.log('%cdrawNodes()', 'color: deepskyblue;');
      }

      // stratify data
      const treeData = d3.stratify()
        .id(d => d.label)
        .parentId(d => d.parent)(nodeData);

      // get nodes in hierarchical structure
      const nodesHierarchy = d3.hierarchy(treeData, d => d.children);

      function updateNodes(nodes, graph) {
        const LAYER_GAP = 110;
        let rootOffset;
        let horizontalDistance;

        // check what tree we are updating and
        // set corresponding offset and horizontal distance
        if (nodes.data.id === 'firms') {
          rootOffset = graph.firmsTreeOffset;
          horizontalDistance = graph.firmsTreeDirection * 110;
        } else if (nodes.data.id === 'consumers') {
          rootOffset = graph.consumersTreeOffset;
          horizontalDistance = graph.consumersTreeDirection * 110;
        }

        if (log) {
          // console.log('%cRoot node: ' + nodes.data.id, 'color: pink;');
          if (horizontalDistance > 0) {
            // console.log('%cDrawing in positive x direction', 'color: pink;');
          } else {
            // console.log('%cDrawing in negative x direction', 'color: pink;');
          }
        }

        // create joins
        const nodesJoin = graph.stage.selectAll(`.node--${nodes.data.id}`)
          .data(nodes.descendants());

        let previousDepth = 0;
        let layerIterator = 0;
        const additionalLayerGap = 50;
        let accumulatedLayerGap = -additionalLayerGap;

        // exit join
        nodesJoin.exit().remove();

        // add group elements in enter join
        const group = nodesJoin.enter()
          .append('g')
          .attr('class', d => `node node--${nodes.data.id} ${(d.children ? 'node--branch' : 'node--leaf')}`);

        // merge enter join with update
        // transform nodes to calculated position
        nodesJoin
          .merge(group)
          .attr('transform', (d, i) => {
            // set x coordinate
            if (d.depth === previousDepth && i !== 0) {
              layerIterator += 1;
              d.data.x = rootOffset[0] + (layerIterator * horizontalDistance);
            } else {
              if (log) {
                // console.log('Create new layer');
              }
              d.data.x = rootOffset[0];
              layerIterator = 0;
              accumulatedLayerGap += 50;
            }

            // set y coordinate
            d.data.y = (LAYER_GAP * i) + accumulatedLayerGap + rootOffset[1];

            // update previousDepth
            previousDepth = d.depth;

            // update nodeCoordinates for later use
            // in drawLinks function
            graph.nodeCoordinates[d.data.id] = { x: d.data.x, y: d.data.y };

            if (log) {
              // console.log(d.data.id + ' vector: ' + d.data.x + ', ' + d.data.y);
            }

            return `translate(${d.data.x}, ${d.data.y})`;
          });

        // append node links
        group
          .append('path')
          .attr('class', 'node__link')
          .attr('d', (d, i) => {
            if (i > 0) {
              if (log) {
                // console.log(0, 0, d.data.x, d.data.y, d.parent.data.x, d.parent.data.y);
              }
              return `M 0 0 L${d.parent.data.x - d.data.x} ${d.parent.data.y - d.data.y}`;
            }
            return '';
          });

        // append node to node group
        group
          .append('circle')
          .attr('class', 'node__circle')
          .attr('cx', 0)
          .attr('cy', 0)
          .attr('r', graph.NODE_RADIUS);

        // append labels to node group
        group
          .append('text')
          .attr('class', 'node__text')
          .attr('text-anchor', 'middle')
          .attr('dy', 5)
          .text(d => d.data.id);
      }

      // update nodes after prepping data
      updateNodes(nodesHierarchy, this.graph);

      // add click events to nodes
      // this.addClickToNodes();

      if (log) {
        // console.log('%cend', 'color: deepskyblue;');
        // console.log(' ');
      }

      return nodesHierarchy;
    },
    drawLinks(links) {
      const log = false;

      if (log) {
        // console.log('%cdrawLinks()', 'color: deepskyblue;');
      }

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

      // remove all groups and defs
      d3.selectAll('.links__wrapper').remove();
      d3.selectAll('defs').remove();

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

            if (log) {
              // console.log('%cCreating new group node on iteration ' + i, 'color: pink;');
            }
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
            if (log) {
              // console.log('Z rotation: ' + Math.round(alpha) + 'deg');
              // console.log('Delta vector: ' + deltaX + ', ' + deltaY);
            }

            group
              .attr('transform', `translate(${globalSourceX}, ${globalSourceY}) rotate(${alpha})`);

            currentSource = d.source;
            currentDestination = d.destination;
          }

          if (log) {
            // console.log('Link ' + i + ' goes from ' + d.source + ' to ' + d.destination);
            // console.log('Link weight: ' + d.weight);
          }

          const j = localEdgeCount + 2;
          const deltaXLocal = deltaX / Math.cos(alpha * Math.PI / 180);
          // 0.3 rad ^= 17.2 deg
          const x0 = xSource + (this.graph.NODE_RADIUS * Math.cos((localEdgeCount + 1) * 0.3));
          const y0 = ySource - (this.graph.NODE_RADIUS * Math.sin((localEdgeCount + 1) * 0.3));
          let x1 = deltaXLocal;
          x1 -= ((this.graph.NODE_RADIUS + 10) * Math.cos((localEdgeCount + 1) * 0.3));
          const y1 = -(this.graph.NODE_RADIUS + 10) * Math.sin((localEdgeCount + 1) * 0.3);

          const cx0 = j * x0;
          const cx1 = (j * (x1 - deltaXLocal)) + deltaXLocal;
          const cy0 = j * y0;
          const cy1 = j * y1;

          // append bezier control points
          // group.append('circle')
          //   .attr('cx', x0)
          //   .attr('cy', y0)
          //   .attr('r', 5)
          //   .attr('fill', 'red');
          // group.append('circle')
          //   .attr('cx', x1)
          //   .attr('cy', y1)
          //   .attr('r', 10)
          //   .attr('fill', 'green');
          // group.append('circle')
          //   .attr('cx', cx0)
          //   .attr('cy', cy0)
          //   .attr('r', 5)
          //   .attr('fill', 'deepskyblue');
          // group.append('circle')
          //   .attr('cx', cx1)
          //   .attr('cy', cy1)
          //   .attr('r', 3)
          //   .attr('fill', 'deepskyblue');
          // group.append('path').attr('d', `M ${cx0} ${cy0} L ${xSource} ${ySource}`);
          // group.append('path').attr('d', `M ${cx1} ${cy1} L ${deltaXLocal} 0`);


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
          .attr('refX', 1)
          .attr('refY', 0)
          .attr('markerWidth', 14)
          .attr('markerHeight', 14)
          .attr('orient', 'auto')
          .attr('markerUnits', 'userSpaceOnUse')
        .append('path')
          .attr('d', 'M0,-5L10,0L0,5');

      if (log) {
        // console.log('%cend', 'color: deepskyblue;');
        // console.log(' ');
      }
    },
  },
  mounted() {
    // set stage
    this.graph.stage = d3.select('body')
      .append('svg')
      .attr('xmlns', 'http://www.w3.org/2000/svg')
      .attr('class', 'tradeview');

    this.consumersTree = this.drawNodes(this.graphData.consumers);
    this.firmsTree = this.drawNodes(this.graphData.firms);

    this.drawLinks(this.graphData.edges);
  },
};
</script>

<style lang="sass">
$blue:                                     #33ccff
$coral:                                    #ff6557
$green:                                    #97e582
$grey:                                     #676767

body
  margin: 20px
  background-color: #f0f0f0

h1
  font: bold 26px/1 Helvetica, Arial, sans-serif
  text-transform: uppercase

.tradeview
  width: 100%
  height: 1000px
  background-color: white

.node

  &__link
    stroke-width: 5px
    opacity: .3

  &__text
    font: bold 14px/1 Helvetica, Arial sans-serif
    text-transform: uppercase
    fill: white

  &--firms
    .node
      &__circle
        fill: $coral
      &__link
        stroke: $coral

  &--consumers
    .node
      &__circle
        fill: $blue
      &__link
        stroke: $blue

  &--branch
    cursor: pointer
    .node
      &__circle
        stroke-width: 4px
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
  stroke: $grey
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
  fill: $grey
  stroke-width: 2px

@keyframes pulsate
  0%
    opacity: 1
  30%
    opacity: .3
  100%
    opacity: 1
</style>
