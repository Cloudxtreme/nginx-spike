vertx.createHttpServer().requestHandler{req ->
println(req.path)

  if(req.path.startsWith('/snail')) Thread.sleep(5000) 
  if(req.path.startsWith('/tortoise')) Thread.sleep(2000) 
  if(req.path.startsWith('/hare')) Thread.sleep(100)  

  req.response.end("Done")
}.listen(8080, 'localhost')
