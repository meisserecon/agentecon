class Tradeview {
  constructor(options) {
    this.stage;
    this.firmsTree;
    this.consumersTree;
    // object that stores coordinates of all nodes
    // used to draw links between nodes
    this.nodeCoordinates = {};
    this.NODE_RADIUS = 50;
    this.data = {
      firms: [
        { label: 'firms', children: 4, parent: '' },
        { label: 'firm0', children: 4, parent: 'firms' },
        { label: 'firm1', children: 4, parent: 'firms' }
      ],
      consumers: [
        { label: 'consumers', children: 4, parent: '' },
        { label: 'consumer0', children: 4, parent: 'consumers' },
        { label: 'consumer1', children: 4, parent: 'consumers' },
        { label: 'consumer2', children: 4, parent: 'consumers' },
        { label: 'consumer3', children: 4 , parent: 'consumer1'},
        { label: 'consumer4', children: 4 , parent: 'consumer3'},
        { label: 'consumer5', children: 4 , parent: 'consumer3'}
      ],
      edges: [
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm0', destination: 'consumer0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer0', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm1', destination: 'consumer0' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer4', destination: 'firm1' },
        { label: '35 input 3 @ 3.81$', weight: 7, source: 'consumer0', destination: 'firm1' },
        { label: '35 input 3 @ 3.81$', weight: 7, source: 'consumer0', destination: 'firm1' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer2', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 5, source: 'firm0', destination: 'consumer2' },
        { label: '35 input 3 @ 3.81$', weight: 5, source: 'firm0', destination: 'consumer2' },
        { label: '35 input 3 @ 3.81$', weight: 10, source: 'firm1', destination: 'consumer2' },
        { label: '35 input 3 @ 3.81$', weight: 10, source: 'firm1', destination: 'consumer2' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 3, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 6, source: 'consumer5', destination: 'firm0' },
        { label: '35 input 3 @ 3.81$', weight: 8, source: 'consumer4', destination: 'firm0' }
      ]
    }

    this.init();
    this.consumersTree = this.drawNodes(this.data.consumers, [350, 100], -1);
    this.firmsTree = this.drawNodes(this.data.firms, [700, 100], +1);
    // TODO: temp disable for node positioning
    this.drawLinks(this.data.edges);
  }

  init() {
    console.log('%cinit()', 'color: deepskyblue;');

    // set stage
    this.stage = d3.select('body')
      .append('svg')
      .attr('xmlns', 'http://www.w3.org/2000/svg')
      .attr('class', 'tradeview')

    console.log('Prepare stage');
    console.log(' ');
  }

  drawNodes(nodeData, offset, direction) {
    console.log('%cdrawNodes()', 'color: deepskyblue;');

    let LAYER_GAP = 110,
        ROOT_OFFSET = offset || [300, 100],
        horizontalDistance = (direction === -1 ? -110 : 110), // to draw to the left, use negative values
        _this = this;

    // stratify data
    let treeData = d3.stratify()
      .id(function(d) { return d.label; })
      .parentId(function(d) { return d.parent; })
      (nodeData);

    // get nodes in hierarchical structure
    let nodes = d3.hierarchy(treeData, function(d) { return d.children; })

    let nodesEnterJoin = this.stage.selectAll('.node__' + nodes.data.id)
      .data(nodes.descendants())
      .enter();

    console.log('%cRoot node: ' + nodes.data.id, 'color: pink;');
    if (horizontalDistance > 0) {
      console.log('%cDrawing in positive x direction', 'color: pink;');
    } else {
      console.log('%cDrawing in negative x direction', 'color: pink;');
    }

    let previousDepth = 0,
        layerIterator = 0,
        additionalLayerGap = 50,
        accumulatedLayerGap = -additionalLayerGap;

    // calculate translation for group
    let group = nodesEnterJoin
      .append('g')
      .attr('transform', function(d, i) {

        // set x coordinate
        if (d.depth === previousDepth && i !== 0) {
          layerIterator++;
          d.data.x = ROOT_OFFSET[0] + layerIterator * horizontalDistance;
        } else {
          console.log('Create new layer');
          d.data.x = ROOT_OFFSET[0];
          layerIterator = 0;
          accumulatedLayerGap+= 50;
        }

        // set y coordinate
        d.data.y = LAYER_GAP * i + accumulatedLayerGap + ROOT_OFFSET[1];

        // update previousDepth
        previousDepth = d.depth;

        // update nodeCoordinates for later use
        // in drawLinks function
        _this.nodeCoordinates[d.data.id] = { x: d.data.x, y: d.data.y};

        console.log(d.data.id + ', x:' + d.data.x + ', y:' + d.data.y);

        return 'translate(' + d.data.x + ', ' + d.data.y + ')';
      });

    // append node to node group
    group
      .append('circle')
      .attr('class', function(d) { return 'node node__' + nodes.data.id; })
      .attr('cx', 0)
      .attr('cy', 0)
      .attr('r', this.NODE_RADIUS);

    // append labels to node group
    group
      .append('text')
      .attr('class', 'node-text')
      .attr('text-anchor', 'middle')
      .attr('dy', 5)
      .text(function(d) { return d.data.id; });

    console.log('%cend', 'color: deepskyblue;');
    console.log(' ');

    return nodes;
  }

  drawLinks(links) {
    console.log('%cdrawLinks()', 'color: deepskyblue;');

    let _this = this;
    let currentSource = links[0].source,
        currentDestination = links[0].destination;

    // create initial group to append links to
    let group = this.stage.append('g')
      .attr('class', 'links__wrapper')

    let globalSourceX = 0;
    let globalSourceY = 0;
    let globalDestinationX = 0;
    let globalDestinationY = 0;
    let deltaX = 0;
    let deltaY = 0;
    let alpha = 0;
    let xSource = 0;
    let ySource = 0;
    let xDestination = 0;
    let yDestination = 0;
    let localEdgeCount = 0;

    // create the enter join
    let linksEnterJoin = group.selectAll('.link')
      .data(links)
      .enter()
      .each(function(d, i) {
        if (i !== 0) {
          console.log(' ');
        }

        if (d.source === currentSource && d.destination === currentDestination && i !== 0) {
          localEdgeCount += 1;
        } else {
          console.log('%cCreating new group node on iteration ' + i, 'color: pink;');

          localEdgeCount = 0;

          // create new svg group
          // note: first group has already been created
          if (i !== 0) {
            group = _this.stage.append('g').attr('class', 'links__wrapper');
          }

          globalSourceX = _this.nodeCoordinates[d.source].x;
          globalSourceY = _this.nodeCoordinates[d.source].y;

          globalDestinationX = _this.nodeCoordinates[d.destination].x;
          globalDestinationY = _this.nodeCoordinates[d.destination].y;

          deltaX = globalDestinationX - globalSourceX;
          deltaY = globalDestinationY - globalSourceY;
          let rotationCorrection = 0;

          alpha = Math.atan(deltaY / deltaX) * (180 / Math.PI);

          if (deltaX < 0) {
            rotationCorrection = -180;
          }
          alpha += rotationCorrection;
          console.log('Z rotation: ' + Math.round(alpha) + 'deg');
          console.log('Delta vector: ' + deltaX + ', ' + deltaY);

          group
            .attr('transform', 'translate(' + globalSourceX + ',' + globalSourceY + ') rotate(' + alpha + ')')

          currentSource = d.source;
          currentDestination = d.destination;
        }

        console.log('Link ' + i + ' goes from ' + d.source + ' to ' + d.destination);
        console.log('Link weight: ' + d.weight);

        let j =  localEdgeCount + 2,
            deltaXLocal = deltaX / Math.cos(alpha * Math.PI / 180),
            x0 = xSource + _this.NODE_RADIUS * Math.cos((localEdgeCount + 1) * 0.3), // 0.3 rad ^= 17.2 deg
            y0 = ySource - _this.NODE_RADIUS * Math.sin((localEdgeCount + 1) * 0.3),
            x1 = deltaXLocal - _this.NODE_RADIUS * Math.cos((localEdgeCount + 1) * 0.3),
            y1 = - _this.NODE_RADIUS * Math.sin((localEdgeCount + 1) * 0.3);

        let cx0 = j * x0,
            cx1 = j * (x1 - deltaXLocal) + deltaXLocal,
            cy0 = j * y0,
            cy1 = j * y1;

        // append bezier control points
        group.append('circle').attr('cx', x0).attr('cy', y0).attr('r', 5).attr('fill', 'red');
        group.append('circle').attr('cx', x1).attr('cy', y1).attr('r', 2).attr('fill', 'red');
        group.append('circle').attr('cx', cx0).attr('cy', cy0).attr('r', 5).attr('fill', 'deepskyblue');
        group.append('circle').attr('cx', cx1).attr('cy', cy1).attr('r', 3).attr('fill', 'deepskyblue');
        group.append('path').attr('d', 'M ' + cx0 + ' ' + cy0 + ' L ' + xSource + ' ' + ySource).attr('stroke', 'silver');
        group.append('path').attr('d', 'M ' + cx1 + ' ' + cy1 + ' L ' + deltaXLocal + ' ' + 0).attr('stroke', 'silver');

        // append the bezier curve and marker
        group
          .append('path')
          .attr('class', 'link')
          .attr('d', 'M' + x0 + ' ' + y0 + ' C ' + cx0 + ' ' + cy0 + ', ' + cx1 + ' ' + cy1 + ', ' + x1 + ' ' + y1)
          .attr('stroke-width', d.weight + 'px')
          .attr('marker-end', function() { return 'url(#marker)'; });
      });

      // create reference marker
      this.stage.append('defs')
        .append('marker')
          .attr('id', 'marker')
          .attr('class', 'marker')
          .attr('viewBox', '0 -5 10 10')
          .attr('refX', 6)
          .attr('refY', 0)
          .attr('markerWidth', 14)
          .attr('markerHeight', 14)
          .attr('orient', 'auto')
          .attr('markerUnits', 'userSpaceOnUse')
        .append('path')
          .attr('d', 'M0,-5L10,0L0,5');

    console.log('%cend', 'color: deepskyblue;');
    console.log(' ');
  }
}

this.tradeview = new Tradeview();
