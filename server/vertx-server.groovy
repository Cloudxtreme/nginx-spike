// N.B. When used like this Vert.X is single threaded, so multiple instances are 
// needed for any concurrency
vertx.createHttpServer().requestHandler{req ->
println(req.path)

  if(req.path.startsWith('/snail')) Thread.sleep(5000) 
  if(req.path.startsWith('/tortoise')) Thread.sleep(2000) 
  if(req.path.startsWith('/hare')) Thread.sleep(100)  

  // This is for application level cache control
  // req.response.putHeader('X-Accel-Expires', '15')

  req.response.end("Done")
}.listen(8080, 'localhost')
