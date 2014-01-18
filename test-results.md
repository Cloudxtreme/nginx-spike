# Testing
The setup is
```
                       +--------- Nginx -------------------+
                       |                                   |
     +-----------+     |  +----------+       +----------+  |      +-----------+
     |           |+----|->|Front     |+----->|Back      |+-|----->|Application|
     |  Client   |     |  |End       |       |End       |  |      |Server     |
     | (JMeter)  |<----|-+|Port 80   |<-----+|Port 9080 |<-|-----+|Port 8080  |
     +-----------+     |  +----------+       +----------+  |      +-----------+
                       |                                   |
                       +-----------------------------------+
```

## Summary
The rate-limiting appears effective at limiting the impact of a flood of requests on the application server, but there's a significant cost in the client needing to understand the significance of the 503 response.  If stability is the primary concern then rate-limiting is an effective policy.

Other technologies can perform rate-limiting e.g. [iptables][http://www.cyberciti.biz/faq/iptables-connection-limits-howto/], and load-balancers usually have built-in methods of handling DoS.

Introducing cache headers into the system opens the possibility for significant improvements in throughput, but obviously only where there are clusters of requests for the same resource within the cache-expiry period.  In the abscence of application cache-management, choosing a global 'safe' expiry period e.g. 180s would be a low-cost route, but a more sophisticated analysis of the logs from real systems would reveal the clusters of requests and point to likely expiry policies.

Making clients cache-aware and resilient to rate-limiting can be straightforward, the popular Java library Apache HTTP Client includes a *"a drop-in replacement for a DefaultHttpClient that transparently adds client-side caching"* and has a built-in retry handler for 503 responses, but we don't always have the luxury of controlling all client implementations, nor do all platforms have the same availability of high quality libraries.  Documenting the behaviour of the service under load is obviously an essential part of communicating the API. 

These tests were run with the client and server processes on a single machine, they're not statistically sound for real-world uses cases; the increased latency for multiple clients on a WAN, the vastly wider range of differences in cache expiry, processing cost and sheer bredth of HTTP resources mean that there's no way to draw conclusions about throughput improvements from adding caching, **but** the cost of implementing a simple caching layer is low, and testing will quickly reveal if there are real-world benefits to be gained.
 

## Stress test (DoS protection)
+ 100 concurrent users, 10 repititions
  + /hare (0.1s delay, 3s cache-expiry)
  + 1000 requests per test run

+ Front-end rate-limit 100 r/s (200 r/s burst), Back-end rate limit 10 r/s (50 r/s burst)
  + No caching
    + throughput 23 resp/s, 54% 503 "Service Temporarily Unavailable" responses
    + **Rate limiting restricts the load on the application server, clients must handle the 503 responses appropriately**
  + Server-side caching only
    + throughput 99.3 resp/s, 0% 503 "Service Temporarily Unavailable" responses
    + **5x improvement in throughput, significantly reduced load on the application server, no 503 responses, client network latency is the main cost**
  + Client-side caching only
    + throughput 99.5 resp/s, 0% 503 "Service Temporarily Unavailable" responses
    + **throughput and load on the application server the same as for server-side caching, reduced volume of network calls offset by larger latency for expired cache**
  + Client and server caching
    + throughput 193 resp/s, 0% 503 "Service Temporarily Unavailable" responses
    + **the best of both worlds**
  + Direct to application server (no rate limiting)
    + throughput 39 resp/s, 0% 503 "Service Temporarily Unavailable" responses
    + **The application server bears the full load of all client requests regardless of duplication**
    

    
## Load tests (Caching improvements)
+ 10 concurrent users, 10 repititions
  + /tortoise (2s delay, 10s cache-expiry), /hare (0.1s delay, 3s cache-expiry), /snail (5s delay, 60s cache-expiry) in random order
  + 300 requests per test run
  + Application server with 10 threads
+ Results
  + No caching
    + mean 6874ms, min 104ms, max 19069ms, throughput 1.4 resp/s
    + **The application server is under heavy load, on average requests take up to 50% longer than the slowest response-processing block** 
  + With server-side caching only
    + mean 377ms, min 2ms, max 12005ms, throughput 20 resp/s
    + **12x improvment in throughput, the application server is only getting a fraction of the client requests**
  + With client-side caching only
    + mean 857ms, min 0ms, max 14988ms, throughput 9.2 resp/s
    + **surprisingly lower throughput compared with server-side caching, may be a funny combination of the additional network latency combined with teh cache expiry period?**
  + With client-side and server-side caching
    + mean 418ms, min 0ms, max 12997ms, throughput 16.9 resp/s
    + **respectable throughput but again lower than server-side caching alone**

