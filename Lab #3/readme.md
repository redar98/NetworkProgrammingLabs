# Java HTTP Requests

For this laboratory work, I made use of built-in java.net package. It includes HttpURLConnection class that supports HTTP-specific features. The [class I created](./src/com/redar/httprequests/HTTPRequest.java) takes an url and http method as constructor parameters which creates the connection. Additional parameters (queries) can be added into this request. There is a site called [httpbin.org](http://httpbin.org/) that allows testing different http methods like GET, POST, PUT, DELETE. For some of those methods it returns the query parameters that were sent.


## HttpURLConnection  
The HttpUrlConnection class allows us to **perform basic HTTP requests without the use of any additional libraries**. All the classes that are needed are contained in the java.net package.

The disadvantages of using this method are that **the code can be more cumbersome than other HTTP libraries, and it does not provide more advanced functionalities such as dedicated methods for adding headers or authentication**.

Below are the steps to create a simple Http request using HttpURLConnection java class.


### **1.  Creating a request**  
**A HttpUrlConnection instance is created by using the openConnection() method of the URL class**. This method only creates a connection object, but does not establish the connection yet.

The HttpUrlConnection class is used for all types of requests by setting the requestMethod attribute to one of the values: *GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE*.

```java
URL url = new URL("https://httpbin.org/get");
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");
```

### **2. Adding request parameters**  
To add parameters to a request, we have to set the doOutput property to true, then write a String of the form *param1=value&param2=value* to the OutputStream of the HttpUrlConnection instance:

```java
Map<String, String> parameters = new HashMap<>();
parameters.put("param1", "val");
 
con.setDoOutput(true);
DataOutputStream out = new DataOutputStream(con.getOutputStream());
// ParameterStringBuilder class transforms the HashMap into a single row parameter string
out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
out.flush();
out.close();
```

### **3. Setting request headers**  
Adding headers to a request can be achieved by using the setRequestProperty() method:

```java
con.setRequestProperty("Content-Type", "application/json");
```

To read the value of a header from a connection, we can use the getHeaderField() method:

```java
String contentType = con.getHeaderField("Content-Type");
```

### **4. Configuring time limits**  
HttpUrlConnection class allows **setting the connect and read timeouts**. These values define the interval of time to wait for the connection to the server to be established or data to be available for reading.

To set the timeout values we can use the setConnectTimeout() and setReadTimeout() methods:

```java
con.setConnectTimeout(5000);
con.setReadTimeout(5000);
```

### **5. Handling cookies**  
The java.net package contains classes that ease working with cookies such as CookieManager and HttpCookie.

First, to **read the cookies from a response**, we can retrieve the value of the Set-Cookie header and parse it to a list of HttpCookie objects:

```java
String cookiesHeader = con.getHeaderField("Set-Cookie");
List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
```

Next, we will **add the cookies to the cookie store**:

```java
cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));
```

Let’s check if a cookie called username is present, and if not, we will add it to the cookie store with a value of “john”:

```java
Optional<HttpCookie> usernameCookie = cookies.stream()
        .findAny().filter(cookie -> cookie.getName().equals("username"));
if (usernameCookie == null) {
    cookieManager.getCookieStore().add(null, new HttpCookie("username", "john"));
}
```

Finally, to **add the cookies to the request**, we need to set the Cookie header, after closing and reopening the connection:

```java
con.disconnect();
con = (HttpURLConnection) url.openConnection();
 
con.setRequestProperty("Cookie", 
        StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));
```

### **6. Reading response**  
Reading the response of the request can be done by **parsing the InputStream of the HttpUrlConnection instance**.

**To execute the request we can use the getResponseCode(), connect(), getInputStream() or getOutputStream() methods**:

```java
int status = con.getResponseCode();
```

Finally, let’s read the response of the request and place it in a content String:

```java
BufferedReader in = new BufferedReader(
  new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer content = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    content.append(inputLine);
}
in.close();
```

To **close the connection**, we can use the disconnect() method:

```java
con.disconnect();
```

## **Conclusions**  
An http request can be made using the steps above. The class I created ([HTTPRequest.java](./src/com/redar/httprequests/HTTPRequest.java)) mostly uses these steps to get response from the requests I made to the [httpbin.org](http://httpbin.org/) site. In the response I was able to retrieve the query parameters that I have passed before making the connection.

### Testing Http Requests:

```java
public static void main(String[] args){
    HTTPRequest getRequest = new HTTPRequest("http://httpbin.org/get", RequestType.GET);
    HTTPRequest postRequest = new HTTPRequest("http://httpbin.org/post", RequestType.POST)
            .addParameter("customParameterKey", "someUselessValue")
            .addParameter("requestBy", "redar98");
    HTTPRequest putRequest = new HTTPRequest("http://httpbin.org/put", RequestType.PUT)
            .addParameter("whatIwant", "putResponse");
    HTTPRequest deleteRequest = new HTTPRequest("http://httpbin.org/delete", RequestType.DELETE)
            .addParameter("toyshop", "Kidosz");
    HTTPRequest patchRequest = new HTTPRequest("http://httpbin.org/patch", RequestType.PATCH)
            .addParameter("game", "of thrones");

    testHttpRequest(getRequest);
    testHttpRequest(postRequest);
    testHttpRequest(putRequest);
    testHttpRequest(deleteRequest);
    testHttpRequest(patchRequest);
}

private static void testHttpRequest(HTTPRequest httpRequest) throws IOException {
    String httpResponse = httpRequest.run();
    String responseBody = httpRequest.getRequestBody();

    System.out.println(httpResponse + "\n" + responseBody);
}
```

Response for GET Request:

```
~ GET ~ http://httpbin.org/get | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 256
Date: Sat, 08 Mar 2019 18:04:16 GMT
Content-Type: application/json

{
  "args": {}, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_191"
  }, 
  "origin": "188.138.156.40, 188.138.156.40", 
  "url": "https://httpbin.org/get"
}
```

Response for POST Request:

```
~ POST ~ http://httpbin.org/post | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 484
Date: Sat, 08 Mar 2019 18:04:16 GMT
Content-Type: application/json

{
  "args": {}, 
  "data": "", 
  "files": {}, 
  "form": {
    "customParameterKey": "someUselessValue", 
    "requestBy": "redar98"
  }, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Content-Length": "53", 
    "Content-Type": "application/x-www-form-urlencoded", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_191"
  }, 
  "json": null, 
  "origin": "188.138.156.40, 188.138.156.40", 
  "url": "https://httpbin.org/post"
}
```

Response for PUT Request:

```
~ PUT ~ http://httpbin.org/put | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 369
Date: Sat, 08 Mar 2019 18:04:16 GMT
Content-Type: application/json

{
  "args": {}, 
  "data": "whatIwant=putResponse", 
  "files": {}, 
  "form": {}, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Content-Length": "21", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_191"
  }, 
  "json": null, 
  "origin": "188.138.156.40, 188.138.156.40", 
  "url": "https://httpbin.org/put"
}
```

Response for DELETE Request:

```
~ DELETE ~ http://httpbin.org/delete | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 436
Date: Sat, 08 Mar 2019 18:04:16 GMT
Content-Type: application/json

{
  "args": {}, 
  "data": "", 
  "files": {}, 
  "form": {
    "toyshop": "Kidosz"
  }, 
  "headers": {
    "Accept": "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2", 
    "Content-Length": "14", 
    "Content-Type": "application/x-www-form-urlencoded", 
    "Host": "httpbin.org", 
    "User-Agent": "Java/1.8.0_191"
  }, 
  "json": null, 
  "origin": "188.138.156.40, 188.138.156.40", 
  "url": "https://httpbin.org/delete"
}
```

Response for PATCH Request:

```
~ PATCH ~ http://httpbin.org/patch | RESPONSE:
200 OK
Server: nginx
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Credentials: true
Connection: keep-alive
Content-Length: 0
Access-Control-Max-Age: 3600
Date: Sat, 08 Mar 2019 18:04:17 GMT
Content-Type: text/html; charset=utf-8
Allow: OPTIONS, PATCH
```