var console_container;
var console_canvas;
var cachedLines = [];
var lineIndex = 0;
var fontSize = 12;

function initConsole() {
	console_container = document.getElementById('console-text-container');
	console_canvas = document.getElementById('console-canvas');
	
	// resize the canvas to fill browser window dynamically
	window.addEventListener('resize', resizeCanvas, false);
	
	function resizeCanvas() {
		var new_width = console_container.clientWidth;
		var new_height = console_container.clientHeight;
		setCanvasResolution(new_width, new_height);
		lineIndex = 0;
		addCachedLines();
	}

	resizeCanvas();
}

function addCanvasText(text) {
	var ctx = console_canvas.getContext('2d');
	var lines = getLines(ctx, text);
	for (i in lines) {
		addLineToCanvas(ctx, lines[i]);
		cachedLines.push(lines[i]);
	}
};

function addLineToCanvas(ctx, line) {
	ctx.font = fontSize + "px Consolas";
	ctx.fillStyle = "white";
	ctx.fillText(line, 5, 15 + fontSize * lineIndex);
	lineIndex++;
}

function addCachedLines() {
	var ctx = console_canvas.getContext('2d');
	for (i in cachedLines) {
		addLineToCanvas(ctx, cachedLines[i]);
	}
}

function getPixelRatio() {
    var ctx = console_canvas.getContext("2d"),
        dpr = window.devicePixelRatio || 1,
        bsr = ctx.webkitBackingStorePixelRatio ||
              ctx.mozBackingStorePixelRatio ||
              ctx.msBackingStorePixelRatio ||
              ctx.oBackingStorePixelRatio ||
              ctx.backingStorePixelRatio || 1;
    return dpr / bsr;
}

function setCanvasResolution(w, h, ratio) {
    if (!ratio) { ratio = getPixelRatio(); }
    var can = console_canvas;
    can.width = w * ratio;
    can.height = h * ratio;
    can.style.width = w + "px";
    can.style.height = h + "px";
    can.getContext("2d").setTransform(ratio, 0, 0, ratio, 0, 0);
}

function getLines(ctx, text) {
    var words = text.split(" ");
    var lines = [];
    var currentLine = words[0];

    for (var i = 1; i < words.length; i++) {
        var word = words[i];
        var width = ctx.measureText(currentLine + " " + word).width;
        if (width < console_container.clientWidth) {
            currentLine += " " + word;
        } else {
            lines.push(currentLine);
            currentLine = word;
        }
    }
    lines.push(currentLine);
    return lines;
}