# Install

## Ubuntu
`sudo apt-get install nginx`

## Centos
Install the yum repo
```
[nginx]
name=nginx repo
baseurl=http://nginx.org/packages/centos/$releasever/$basearch/
gpgcheck=0
enabled=1
```

Install nginx

`yum install -y nginx`



### Rate Limiting
+ Restricts requests on a per-user and/or IP basis
+ The Nginx server returns 503 (Service Temporarily Unavailable) when the request limit is exceeded
  + clients need to recognise this as an indication to back-off and retry

### Header re-writing
As an alternative to the application setting its own cache headers, nginx can add "Expires" and "Cache-Control" headers e.g. `expires 5;` would add Response headers:
```
HTTP/1.1 200 OK
...
Expires: Sat, 18 Jan 2014 12:54:15 GMT
Cache-Control: max-age=5
```
    
The client is expected to honour the values in the headers and not attempt to re-try requests for resources until the Expires time is reached

### Caching
+ By layering two Nginx services together, the 'front' one can act upon cache headers set by the one 'behind'
  + this is a cheap way of adding server-side caching as a second best to the application setting its own cache-control headers 
+ N.B. needs to be disabled for long-running/COMET services

## Overview

```
                       +--------- Nginx -------------------+
                       |                                   |
     +-----------+     |  +----------+       +----------+  |      +-----------+
     |           |+----|->|Front     |+----->|Back      |+-|----->|Application|
     |  Client   |     |  |End       |       |End       |  |      |Server     |
     |           |<----|-+|Port 80   |<-----+|Port 9080 |<-|-----+|Port 8080  |
     +-----------+     |  +----------+       +----------+  |      +-----------+
                       |                                   |
                       +-----------------------------------+
```

+ The Front End nginx service acts as a content cache and provides rate limiting per user and per source-IP
  + The cache serves up HITs where possible so no connections are opened on the back end or appliaction servers
  + Cache headers are also passed back to the client to allow client-side caching where supported
  + The rate-limiting fails fast with 503 (Service Temporarliy Unavailable) responses, protecting the other services from excessive load
+ The Back End nginx service sets cache headers on behalf of the application server and provides more restrictive rate limiting
+ The application server is protected from direct interaction with the client(s)
 

## References
+ [http://nginx.org/en/docs/http/ngx_http_limit_req_module.html]
+ [http://nginx.org/en/docs/http/ngx_http_limit_conn_module.html]
+ [http://nginx.org/en/docs/http/ngx_http_headers_module.html]
+ [http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_cache]
