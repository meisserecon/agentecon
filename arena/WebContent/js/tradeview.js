class Tradeview {
  constructor(options) {
    this.stage;
    this.NODE_RADIUS = 50;
    this.nodes = [
      { x: '200', y: '300' },
      { x: '700', y: '200' }
    ];
    this.links = [
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

    let link = this.stage.selectAll('.link')
      .data(this.links)
      .enter()
      .append('path')
      .attr('class', 'link')
      .attr('d', function(d, i) {
        if (i === 0) {
          return 'M' + d.source[0] + ',' + d.source[1] + 'A0,0,0,0,1,' + d.target[0] + ',' + d.target[1];
        } else {
          let oppositeLeg = d.target[1] - d.source[1];
          let adjacentLeg = d.target[0] - d.source[0];
          let alpha = Math.atan(oppositeLeg/adjacentLeg) * (180 / Math.PI);
          return 'M' + d.source[0] + ',' + d.source[1] + 'A100' + ',' + (i % 2 === 0 ? (i-1)*8 : i*8) + ',' + alpha + ',0,' + (i % 2 === 0 ? '0' : '1') + ',' + d.target[0] + ',' + d.target[1];
        }
      })
      .attr('marker-end', function(d) { return 'url(#marker)'; });

    this.stage.append('svg:defs')
      .selectAll('marker')
      .data([true])
      .enter()
      .append('svg:marker')
        .attr('id', 'marker')
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', this.NODE_RADIUS)
        .attr('refY', function(d, i) { return i*100; })
        .attr('markerWidth', 6)
        .attr('markerHeight', 6)
        .attr('orient', 'auto')
        .attr('position', 300)
      .append('svg:path')
        .attr('d', 'M0,-5L10,0L0,5');
  }
}

this.tradeview = new Tradeview();
