class Tradeview {
  constructor(options) {
    this.stage;
    this.firmsTree;
    this.firmsTreeOffset = [700, 100];
    this.firmsTreeDirection = +1;
    this.consumersTree;
    this.consumersTreeOffset = [350, 100];
    this.consumersTreeDirection = -1;
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
        { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 3, source: 'firm1', destination: 'consumer5' },
        { label: '35 input 3 @ 3.81$', weight: 8, source: 'consumer4', destination: 'firm0' }
      ]
    }

    this.init();
    this.consumersTree = this.drawNodes(this.data.consumers, false);
    this.firmsTree = this.drawNodes(this.data.firms, false);
    this.drawLinks(this.data.edges, true);
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

  addClickToNodes() {
    let _this = this;

    // bind click event to nodes
    d3.selectAll('.node')
      .on('click', function(d) {

        // TODO: request new data from server
        // for now, prepped for action after click
        // on consumer1

        // Nodes
        if (_this.data.consumers.length > 4) {
          _this.data.consumers.splice(4);
        } else {
          _this.data.consumers.push(
            { label: 'consumer3', children: 4 , parent: 'consumer1'},
            { label: 'consumer4', children: 4 , parent: 'consumer3'},
            { label: 'consumer5', children: 4 , parent: 'consumer3'}
          );
        }

        _this.consumersTree = _this.drawNodes(_this.data.consumers, false);
        _this.firmsTree = _this.drawNodes(_this.data.firms, false);

        // Links
        if (_this.data.edges.length > 11) {
          _this.data.edges.splice(11);
        } else {
          _this.data.edges.push(
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
            { label: '35 input 3 @ 3.81$', weight: 1, source: 'firm1', destination: 'consumer5' },
            { label: '35 input 3 @ 3.81$', weight: 3, source: 'firm1', destination: 'consumer5' },
            { label: '35 input 3 @ 3.81$', weight: 8, source: 'consumer4', destination: 'firm0' }
          );
        }

        _this.drawLinks(_this.data.edges, false);

        // // only continue when node is not a leaf
        // if (d.children || d.childrenHidden) {
        //   let tree = _this.consumersTree;

        //   d = toggleChildren(d);

        //   if (d3.select(this).classed('node--firms')) {
        //     tree = _this.firmsTree;
        //   }

        //   _this.drawNodes(tree, false);


        //   // TODO: move links of hidden children
        //   // to hidden layer and the drawLinks
        //   // or better: don't draw links with nodes
        //   // that are not defined
        //   _this.drawLinks(_this.data.edges);
        // }
      });
  }

  drawNodes(nodeData, log) {
    if (log) {
      console.log('%cdrawNodes()', 'color: deepskyblue;');
    }

    let _this = this;

    // stratify data
    let treeData = d3.stratify()
      .id(function(d) { return d.label; })
      .parentId(function(d) { return d.parent; })
      (nodeData);

    // get nodes in hierarchical structure
    let nodes = d3.hierarchy(treeData, function(d) { return d.children; })

    function updateNodes(nodes, _this) {

      let LAYER_GAP = 110,
          rootOffset,
          horizontalDistance;

      // check what tree we are updating and
      // set corresponding offset and horizontal distance
      if (nodes.data.id === 'firms') {
        rootOffset = _this.firmsTreeOffset;
        horizontalDistance = _this.firmsTreeDirection * 110;
      } else if (nodes.data.id === 'consumers') {
        rootOffset = _this.consumersTreeOffset;
        horizontalDistance = _this.consumersTreeDirection * 110;
      }

      if (log) {
        console.log('%cRoot node: ' + nodes.data.id, 'color: pink;');
        if (horizontalDistance > 0) {
          console.log('%cDrawing in positive x direction', 'color: pink;');
        } else {
          console.log('%cDrawing in negative x direction', 'color: pink;');
        }
      }

      // create joins
      let nodesJoin = _this.stage.selectAll('.node--' + nodes.data.id)
        .data(nodes.descendants())

      let previousDepth = 0,
          layerIterator = 0,
          additionalLayerGap = 50,
          accumulatedLayerGap = -additionalLayerGap;

      // exit join
      nodesJoin.exit().remove();

      // add group elements in enter join
      let group = nodesJoin.enter()
        .append('g')
        .attr('class', function(d) {
          return 'node node--' + nodes.data.id + (d.children ? ' node--branch' : ' node--leaf');
        });

      // merge enter join with update
      // transform nodes to calculated position
      nodesJoin
        .merge(group)
        .attr('transform', function(d, i) {

          // set x coordinate
          if (d.depth === previousDepth && i !== 0) {
            layerIterator++;
            d.data.x = rootOffset[0] + layerIterator * horizontalDistance;
          } else {
            if (log) {
              console.log('Create new layer');
            }
            d.data.x = rootOffset[0];
            layerIterator = 0;
            accumulatedLayerGap+= 50;
          }

          // set y coordinate
          d.data.y = LAYER_GAP * i + accumulatedLayerGap + rootOffset[1];

          // update previousDepth
          previousDepth = d.depth;

          // update nodeCoordinates for later use
          // in drawLinks function
          _this.nodeCoordinates[d.data.id] = { x: d.data.x, y: d.data.y};

          if (log) {
            console.log(d.data.id + ' vector: ' + d.data.x + ', ' + d.data.y);
          }

          return 'translate(' + d.data.x + ', ' + d.data.y + ')';
        });

      // append node links
      group
        .append('path')
        .attr('class', 'node__link')
        .attr('d', function(d, i) {
          if (i > 0) {
            if (log) {
              console.log(0, 0, d.data.x, d.data.y, d.parent.data.x, d.parent.data.y);
            }
            return 'M 0 0 L' + (d.parent.data.x - d.data.x) + ' ' + (d.parent.data.y - d.data.y);
          }
        });

      // append node to node group
      group
        .append('circle')
        .attr('class', 'node__circle')
        .attr('cx', 0)
        .attr('cy', 0)
        .attr('r', _this.NODE_RADIUS);

      // append labels to node group
      group
        .append('text')
        .attr('class', 'node__text')
        .attr('text-anchor', 'middle')
        .attr('dy', 5)
        .text(function(d) { return d.data.id; });
    }

    // update nodes after prepping data
    updateNodes(nodes, this);

    // add click events to nodes
    this.addClickToNodes();

    if (log) {
      console.log('%cend', 'color: deepskyblue;');
      console.log(' ');
    }

    return nodes;
  }

  drawLinks(links, log) {
    if (log) {
      console.log('%cdrawLinks()', 'color: deepskyblue;');
    }

    let _this = this;
    let currentSource = links[0].source,
        currentDestination = links[0].destination;

    let globalSourceX = 0,
        globalSourceY = 0,
        globalDestinationX = 0,
        globalDestinationY = 0,
        deltaX = 0,
        deltaY = 0,
        alpha = 0,
        xSource = 0,
        ySource = 0,
        xDestination = 0,
        yDestination = 0,
        localEdgeCount = 0;

    // remove all groups and defs
    d3.selectAll('.links__wrapper').remove();
    d3.selectAll('defs').remove();

    // create initial group to append links to
    let group = this.stage.append('g')
      .attr('class', 'links__wrapper')

    // create the enter join
    let linksJoin = group.selectAll('link')
      .data(links);

    // exit join
    linksJoin.exit().remove();

    let linksEnterJoin = linksJoin
      .enter();

    linksJoin
      .merge(linksEnterJoin)
      .each(function(d, i) {
        if (d.source === currentSource && d.destination === currentDestination && i !== 0) {
          localEdgeCount += 1;
        } else {
          localEdgeCount = 0;

          if (log) {
            console.log('%cCreating new group node on iteration ' + i, 'color: pink;');
          }
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
          if (log) {
            console.log('Z rotation: ' + Math.round(alpha) + 'deg');
            console.log('Delta vector: ' + deltaX + ', ' + deltaY);
          }

          group
            .attr('transform', 'translate(' + globalSourceX + ',' + globalSourceY + ') rotate(' + alpha + ')')

          currentSource = d.source;
          currentDestination = d.destination;
        }

        if (log) {
          console.log('Link ' + i + ' goes from ' + d.source + ' to ' + d.destination);
          console.log('Link weight: ' + d.weight);
        }

        let j =  localEdgeCount + 2,
            deltaXLocal = deltaX / Math.cos(alpha * Math.PI / 180),
            x0 = xSource + _this.NODE_RADIUS * Math.cos((localEdgeCount + 1) * 0.3), // 0.3 rad ^= 17.2 deg
            y0 = ySource - _this.NODE_RADIUS * Math.sin((localEdgeCount + 1) * 0.3),
            x1 = deltaXLocal - (_this.NODE_RADIUS + 10) * Math.cos((localEdgeCount + 1) * 0.3),
            y1 = - (_this.NODE_RADIUS + 10) * Math.sin((localEdgeCount + 1) * 0.3);

        let cx0 = j * x0,
            cx1 = j * (x1 - deltaXLocal) + deltaXLocal,
            cy0 = j * y0,
            cy1 = j * y1;

        // append bezier control points
        // group.append('circle').attr('cx', x0).attr('cy', y0).attr('r', 5).attr('fill', 'red');
        // group.append('circle').attr('cx', x1).attr('cy', y1).attr('r', 2).attr('fill', 'red');
        // group.append('circle').attr('cx', cx0).attr('cy', cy0).attr('r', 5).attr('fill', 'deepskyblue');
        // group.append('circle').attr('cx', cx1).attr('cy', cy1).attr('r', 3).attr('fill', 'deepskyblue');
        // group.append('path').attr('d', 'M ' + cx0 + ' ' + cy0 + ' L ' + xSource + ' ' + ySource).attr('stroke', 'silver');
        // group.append('path').attr('d', 'M ' + cx1 + ' ' + cy1 + ' L ' + deltaXLocal + ' ' + 0).attr('stroke', 'silver');


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
          .attr('refX', 1)
          .attr('refY', 0)
          .attr('markerWidth', 14)
          .attr('markerHeight', 14)
          .attr('orient', 'auto')
          .attr('markerUnits', 'userSpaceOnUse')
        .append('path')
          .attr('d', 'M0,-5L10,0L0,5');

    if (log) {
      console.log('%cend', 'color: deepskyblue;');
      console.log(' ');
    }
  }
}

this.tradeview = new Tradeview();
