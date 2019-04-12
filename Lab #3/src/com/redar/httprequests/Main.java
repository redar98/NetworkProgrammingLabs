package com.redar.httprequests;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        try {
            HTTPRequest getRequest = new HTTPRequest("http://httpbin.org/get", RequestType.GET);
            HTTPRequest postRequest = new HTTPRequest("http://httpbin.org/post", RequestType.POST)
                    .addParameter("customParameterKey", "someUselessValue")
                    .addParameter("requestBy", "redar98");
            HTTPRequest putRequest = new HTTPRequest("http://httpbin.org/put", RequestType.PUT)
                    .addParameter("whatIwant", "putResponse");
            HTTPRequest deleteRequest = new HTTPRequest("http://httpbin.org/delete", RequestType.DELETE)
                    .addParameter("toyshop", "Kidosz");
            HTTPRequest patchRequest = new HTTPRequest("http://httpbin.org/patch", RequestType.PATCH);

            HTTPRequest authRequest = new HTTPRequest("http://httpbin.org/basic-auth", RequestType.GET)
                    .addParameter("user", "deniz")
                    .addParameter("passwd", "password");

            testHttpRequest(getRequest);
            testHttpRequest(postRequest);
            testHttpRequest(putRequest);
            testHttpRequest(deleteRequest);
            testHttpRequest(patchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testHttpRequest(HTTPRequest httpRequest) throws IOException {
        String httpResponse = httpRequest.run();
        String responseBody = httpRequest.getRequestBody();

        System.out.println(httpResponse + "\n" + responseBody);
    }
}
