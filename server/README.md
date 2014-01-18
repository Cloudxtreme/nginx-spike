# The stub server
The server is a simple Vert.X instance configured to block for a 

## Preparation
Install GVM tool
`curl -s get.gvmtool.net | bash`

Install vertx (2.0.2-final)
`gvm install vertx`

## Running
Run the server (10 handlers)
`vertx run vertx-server.groovy -instances 10`


+ http://localhost:8080/snail blocks for 5s
+ http://localhost:8080/tortoise blocks for 2s
+ http://localhost:8080/hare blocks for 0.1s
+ Any other path doesn't block at all


