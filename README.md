# J2Node
Demonstration of efficient inter-process connection between Node & Java

### Steps to run the prototype
* Run the Node process `node src/server.js`
* Run the Java process under `src/Server.java` to run benchmark

### Sample Benchmark Results on Late 2013 MacBook Pro
Echo Tests Duration Summary (N=1000):

| **Message Size (KB)** | **Messaging Latency (ms)**|
| :-------------------: | :------------------------:|
| 1                     | 0.06                      |
| 10                    | 0.08                      |
| 100                   | 0.16                      |
| 200                   | 0.26                      |
| 300                   | 0.42                      |
| 400                   | 0.55                      |
| 500                   | 0.74                      |
| 800                   | 1.00                      |
| 1024                  | 1.46                      |