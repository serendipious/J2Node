const fs = require('fs');
const net = require('net');

const HOST = '127.0.0.1'; // parameterize the IP of the Listen
const PORT = 6969; // TCP LISTEN port

console.info("Reading test payloads to prepare for test ...");
const TEST_PAYLOAD_DIR = 'test_payloads';
const TEST_PAYLOADS = {};
fs.readdirSync(TEST_PAYLOAD_DIR).forEach((filename) => {
  let payloadSize = filename.replace('kb', '');
  TEST_PAYLOADS[payloadSize] = fs.readFileSync(`${TEST_PAYLOAD_DIR}/${filename}`).toString().trim();
});
console.info("Payloads read. Ready for test!");

const TEST_PAYLOAD_KB_SIZE = 1024;

// Create an instance of the Server and waits for connection
net.createServer(function(sock) {
   let message;
   let isFullMessage;

  // Receives a connection - a socket object is associated to the connection automatically
  console.log(`[${Date.now()}] CONNECTED: ${sock.remoteAddress} ${sock.remotePort}`);

  // Add a 'data' - "event handler" in this socket instance
  sock.on('data', function(data) {
	  // data was received in the socket 
	  // Writes the received message back to the socket (echo)
	  sock.write(data);

      // console.log(`Reading message with header: ${data.slice(0,10).toString()} of size: ${data.length}`);
	  if (data.slice(0,1).toString() === '{') {
	    // console.log("Message start identified");
	    message = data;
	    isFullMessage = false;
	  }
	  else {
	    message = Buffer.concat([message, data]);
	  }

	  let dataLength = data.length;
	  if (data.slice(dataLength-2, dataLength-1).toString() === '}') {
	    // console.log("Message end identified");
	    isFullMessage = true;
	  }
  });

  // Add a 'close' - "event handler" in this socket instance
  sock.on('close', function(data) {
    // Close connection
    console.log(`[${Date.now()}] CLOSED ${sock.remoteAddress} ${sock.remotePort}`);

    let messageStr = message.toString().trim();
    let testPayloadStr = TEST_PAYLOADS[`${TEST_PAYLOAD_KB_SIZE}`].trim();
    console.log(`
        * isFullMessage: ${isFullMessage}
        * messageCheck: ${messageStr === testPayloadStr}
        * message: ${messageStr.length}
        * test-payload: ${testPayloadStr.length}
    `);
  });


}).listen(PORT, HOST);


console.log('Server listening on ' + HOST +':'+ PORT);