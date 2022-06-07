var socketToServer = null;

function connectToServer(ip, port) {
	if (isConnectedToServer()) return;
	if (port == null) port = "";
	socketToServer = new WebSocket("ws://" + ip + ":" + port);
	socketToServer.onopen = event => onConnectedToServer(event);
	socketToServer.onclose = event => onDisconnectedFromServer(event);
	socketToServer.onmessage = event => onMessageFromServer(event);
	socketToServer.onerror = event => onErrorWithServer(event);
}

function isConnectedToServer() {
	return socketToServer != null;
}

function onConnectedToServer(event) {
	console.log("Connected to WebSocketServer on " + socketToServer.url + ".")
}

function onDisconnectedFromServer(event) {
	socketToServer = null;
	console.log("Disconnected from WebSocketServer.");
}

function onMessageFromServer(event) {
	console.log("Message received from WebSocketServer: " + event.data);
}

function onErrorWithServer(event) {
	console.log("Error: " + event);
}
