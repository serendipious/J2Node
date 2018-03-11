const net = require('net');

const HOST = '127.0.0.1'; // parameterize the IP of the Listen
const PORT = 6969; // TCP LISTEN port

// Create an instance of the Server and waits for a conexão
net.createServer(function(sock) {
  // Receives a connection - a socket object is associated to the connection automatically
  console.log(`[${Date.now()}] CONNECTED: ${sock.remoteAddress} ${sock.remotePort}`);

  // Add a 'data' - "event handler" in this socket instance
  sock.on('data', function(data) {
	  // data was received in the socket 
	  // Writes the received message back to the socket (echo)
	  sock.write(data);
  });

  // Add a 'close' - "event handler" in this socket instance
  sock.on('close', function(data) {
	  // closed connection
    console.log(`[${Date.now()}] CLOSED ${sock.remoteAddress} ${sock.remotePort}`);
  });


}).listen(PORT, HOST);


console.log('Server listening on ' + HOST +':'+ PORT);