class Tradeview {
  constructor(options) {
    this.stage;
    this.NODE_RADIUS = 50;
    this.nodes = [
      { x: '200', y: '300' },
      { x: '700', y: '300' }
    ];
    this.links = [
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] }
    ];

    this.init();
    this.updateNodes();
    this.updateLinks();
  }

  init() {
    console.log('init TradeView');

    let _this = this;

    // set stage
    this.stage = d3.select('body')
      .append('svg')
      .attr('xmlns', 'http://www.w3.org/2000/svg')
      .attr('class', 'tradeview');

    d3.select('button')
      .on('click', function() {

        let iv0 = setInterval(function() {
          _this.links.push({ source: [_this.nodes[0].x,_this.nodes[0].y], target: [_this.nodes[1].x, _this.nodes[1].y] })
        }, 1000);

        let iv1 = setInterval(function() {
          _this.updateLinks();
        }, 1005);
      });
  }

  updateNodes() {
    console.log('updating nodes');

    let nodes = this.stage.selectAll('.node')
      .data(this.nodes)
      .enter()
      .append('circle')
      .attr('class', 'node')
      .attr('cx', function(d) { return d.x; })
      .attr('cy', function(d) { return d.y; })
      .attr('r', this.NODE_RADIUS);

    // node event listeners
    nodes.on('click', function(d) {
      d3.select(this)
        .classed('active', true);
    });

    let type;
    let author;
    let instance;
  }

  updateLinks() {
    console.log('updating links');

    let _this = this;

    let link = this.stage.selectAll('.link')
      .data(this.links)
      .enter()
      .append('path')
      .attr('class', 'link')
      .attr('d', function(d, i) {
        let x0 = parseInt(d.source[0], 10) + _this.NODE_RADIUS * Math.cos(i * 0.3);
        let y0 = parseInt(d.source[1], 10) - _this.NODE_RADIUS * Math.sin(i * 0.3);
        let x1 = parseInt(d.target[0], 10) - _this.NODE_RADIUS * Math.cos(i * 0.3);
        let y1 = parseInt(d.target[1], 10) - _this.NODE_RADIUS * Math.sin(i * 0.3);

        let cx0 = (2 + i * 0.1) * x0 - d.source[0];
        let cx1 = (2 - i * 0.01) * x1 - d.target[0];
        let cy0 = (2 - i * 0.1) * y0 - d.source[1];
        let cy1 = (2 - i * 0.07) * y1 - d.target[1];

        if (i === 0) {
          cy0 = y0 - _this.NODE_RADIUS / 2;
          cy1 = y1 - _this.NODE_RADIUS / 2;
        }

        _this.stage
          .append('circle')
          .attr('cx', x0)
          .attr('cy', y0)
          .attr('r', 5)
          .attr('fill', 'red');

        _this.stage
          .append('circle')
          .attr('cx', x1)
          .attr('cy', y1)
          .attr('r', 2)
          .attr('fill', 'red');

        _this.stage
          .append('circle')
          .attr('cx', cx0)
          .attr('cy', cy0)
          .attr('r', 5)
          .attr('fill', 'deepskyblue');

        _this.stage
          .append('circle')
          .attr('cx', cx1)
          .attr('cy', cy1)
          .attr('r', 5)
          .attr('fill', 'deepskyblue');

        let oppositeLeg = d.target[1] - d.source[1];
        let adjacentLeg = d.target[0] - d.source[0];
        let alpha = Math.atan(oppositeLeg/adjacentLeg) * (180 / Math.PI);
        return 'M' + x0 + ' ' + y0 + ' C ' + cx0 + ' ' + cy0 + ', ' + cx1 + ' ' + cy1 + ', ' + x1 + ' ' + y1;
      })
      .attr('marker-end', function(d) { return 'url(#marker)'; });

    this.stage.append('svg:defs')
      .selectAll('marker')
      .data([true])
      .enter()
      .append('svg:marker')
        .attr('id', 'marker')
        .attr('class', 'marker')
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', 9)
        .attr('refY', 0)
        .attr('markerWidth', 6)
        .attr('markerHeight', 6)
        .attr('orient', 'auto')
      .append('svg:path')
        .attr('d', 'M0,-5L10,0L0,5');
  }
}

this.tradeview = new Tradeview();
