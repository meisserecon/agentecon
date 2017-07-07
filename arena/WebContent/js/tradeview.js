class Tradeview {
  constructor(options) {
    this.stage;
    this.NODE_RADIUS = 50;
    this.data = {
      nodes: [
        { label: 'firm', children: 4 },
        { label: 'consumer0', children: 4 },
        { label: 'consumer1', children: 4 },
        { label: 'consumer2', children: 4 },
        { label: 'consumer3', children: 4 }
      ],
      edges: [
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm', destination: 'consumer0' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm', destination: 'consumer0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm', destination: 'consumer0' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm', destination: 'consumer1' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'firm', destination: 'consumer2' },
        { label: '35 input 3 @ 3.81$', weight: 4, source: 'firm', destination: 'consumer3' },
        { label: '35 input 3 @ 3.81$', weight: 7, source: 'consumer0', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 3, source: 'consumer1', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer2', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 10, source: 'consumer3', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'consumer3', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer3', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 2, source: 'consumer3', destination: 'firm' },
        { label: '35 input 3 @ 3.81$', weight: 8, source: 'consumer3', destination: 'firm' }
      ]
    }

    this.nodeCoordinates = {
      firm: { x: '500', y: '400' },
      consumer0: { x: '700', y: '200' },
      consumer1: { x: '300', y: '250' },
      consumer2: { x: '300', y: '550' },
      consumer3: { x: '800', y: '550' }
    }

    this.init();
    this.drawNodes(this.data.nodes, this.nodeCoordinates);
    this.drawLinks(this.data.edges);
  }

  init() {
    console.log('%cinit()', 'color: deepskyblue;');
    console.log(' ');

    // set stage
    this.stage = d3.select('body')
      .append('svg')
      .attr('xmlns', 'http://www.w3.org/2000/svg')
      .attr('class', 'tradeview')
  }

  drawNodes(nodes, coordinates) {
    console.log('%cdrawNodes()', 'color: deepskyblue;');

    let nodesEnterJoin = this.stage.selectAll('.node')
      .data(nodes)
      .enter();

    let group = nodesEnterJoin
      .append('g')
      .attr('transform', function(d) { return 'translate(' + coordinates[d.label].x + ',' + coordinates[d.label].y + ')'; })
      .each(function(d, i) { console.log(d.label + ' coordinates: ' + coordinates[d.label].x + ', ' + coordinates[d.label].y) });

    // append node to node group
    group
      .append('circle')
      .attr('class', 'node')
      .attr('cx', 0)
      .attr('cy', 0)
      .attr('r', this.NODE_RADIUS);

    // append labels to node group
    group
      .append('text')
      .attr('class', 'node-text')
      .attr('text-anchor', 'middle')
      .attr('dy', 5)
      .text(function(d) { return d.label; });

    console.log('%cend', 'color: deepskyblue;');
    console.log(' ');
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
