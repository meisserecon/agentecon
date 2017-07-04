class Tradeview {
  constructor(options) {
    this.stage;
    this.NODE_RADIUS = 50;
    this.nodes = [
      { x: '200', y: '500' },
      { x: '700', y: '400' }
    ];
    this.links = [
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      { source: [this.nodes[0].x,this.nodes[0].y], target: [this.nodes[1].x, this.nodes[1].y] },
      // { source: [this.nodes[1].x,this.nodes[1].y], target: [this.nodes[0].x, this.nodes[0].y] },
      // { source: [this.nodes[1].x,this.nodes[1].y], target: [this.nodes[0].x, this.nodes[0].y] },
      // { source: [this.nodes[1].x,this.nodes[1].y], target: [this.nodes[0].x, this.nodes[0].y] }
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

    let oppositeLeg = this.nodes[1].y - this.nodes[0].y;
    let adjacentLeg = this.nodes[1].x - this.nodes[0].x;
    let alphaRad = Math.atan(oppositeLeg/adjacentLeg) * (180 / Math.PI);

    let linksWrapper = this.stage.append('g')
      .attr('class', 'links__wrapper')
      .attr('transform', 'translate(' + this.nodes[0].x + ',' + this.nodes[0].y + ') rotate(' + alphaRad + ')');

    linksWrapper.append('path')
      .attr('d', 'M0 0 L500 0')
      .attr('stroke', 'pink');

    // coordinates for target node in local coordinate system
    let xSource = 0;
    let ySource = 0;
    let xTarget = (this.nodes[1].x - this.nodes[0].x)/Math.cos(alphaRad * Math.PI / 180);
    let yTarget = 0;

    linksWrapper.append('circle')
      .attr('cx', xTarget)
      .attr('cy', yTarget)
      .attr('r', 10)
      .attr('fill', 'black');

    let link = linksWrapper.selectAll('.link')
      .data(this.links)
      .enter()
      .append('path')
      .attr('class', 'link')
      .attr('d', function(d, i) {
        let j = i + 2;
        let x0 = xSource + _this.NODE_RADIUS * Math.cos((i + 1) * 0.3); // 0.3 rad ^= 17.2 deg
        let y0 = ySource - _this.NODE_RADIUS * Math.sin((i + 1) * 0.3);
        let x1 = xTarget - _this.NODE_RADIUS * Math.cos((i + 1) * 0.3);
        let y1 = yTarget - _this.NODE_RADIUS * Math.sin((i + 1) * 0.3);

        let cx0 = j * (x0);
        let cx1 = j * (x1 - xTarget) + xTarget;
        let cy0 = j * (y0);
        let cy1 = j * (y1 - yTarget);

        linksWrapper
          .append('circle')
          .attr('cx', x0)
          .attr('cy', y0)
          .attr('r', 5)
          .attr('fill', 'red');

        linksWrapper
          .append('circle')
          .attr('cx', x1)
          .attr('cy', y1)
          .attr('r', 2)
          .attr('fill', 'red');

        linksWrapper
          .append('circle')
          .attr('cx', cx0)
          .attr('cy', cy0)
          .attr('r', 5)
          .attr('fill', 'deepskyblue');

        linksWrapper
          .append('circle')
          .attr('cx', cx1)
          .attr('cy', cy1)
          .attr('r', 3)
          .attr('fill', 'deepskyblue');

        return 'M' + x0 + ' ' + y0 + ' C ' + cx0 + ' ' + cy0 + ', ' + cx1 + ' ' + cy1 + ', ' + x1 + ' ' + y1;
      })
      .attr('marker-end', function(d) { return 'url(#marker)'; });

    this.stage.append('svg:defs')
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
