# Install

## Ubuntu
`sudo apt-get install nginx`

## Centos

Install the yum repo

Install nginx
`yum install -y nginx`


## References
+ [http://nginx.org/en/docs/http/ngx_http_limit_req_module.html]
+ [http://nginx.org/en/docs/http/ngx_http_limit_conn_module.html]
+ [http://nginx.org/en/docs/http/ngx_http_headers_module.html]
+ [http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_cache]

### Rate Limiting
+ Restricts requests on a per-user and/or IP basis
+ The Nginx server returns 503 (Service Temporarily Unavailable) when the request limit is exceeded
  + clients need to recognise this as an indication to back-off and retry

### Header re-writing
As an alternative to the application setting its own cache headers, nginx can add "Expires" and "Cache-Control" headers e.g. `expires 5;` would add Response headers:
    `HTTP/1.1 200 OK
    ...
    Expires: Sat, 18 Jan 2014 12:54:15 GMT
    Cache-Control: max-age=5`
    
The client is expected to honour the values in the headers and not attempt to re-try requests for resources until the Expires time is reached

### Caching
+ N.B. needs to be disabled for long-running/COMET services
