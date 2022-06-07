var socketToMaster = null;

function connectToMaster(ip, port) {
	if (isConnectedToMaster()) return;
	if (port == null) port = "";
	socketToMaster = new WebSocket("ws://" + ip + ":" + port);
	socketToMaster.onopen = event => onConnectedToMaster(event);
	socketToMaster.onclose = event => onDisconnectedFromMaster(event);
	socketToMaster.onmessage = event => onMessageFromMaster(event);
	socketToMaster.onerror = event => onErrorWithMaster(event);
}

function isConnectedToMaster() {
	return socketToMaster != null;
}

function onConnectedToMaster(event) {
	console.log("Connected to WebSocketServer on " + socket.url + ".")
}

function onDisconnectedFromMaster(event) {
	socketToMaster = null;
	console.log("Disconnected from WebSocketServer.");
}

function onMessageFromMaster(event) {
	console.log("Message received from WebSocketServer: " + event.data);
}

function onErrorWithMaster(event) {
	console.log("Error: " + event);
}
